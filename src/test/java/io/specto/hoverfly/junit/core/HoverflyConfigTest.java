package io.specto.hoverfly.junit.core;

import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static org.assertj.core.api.Assertions.assertThat;


public class HoverflyConfigTest {

    @Rule
    public EnvironmentVariables envVars = new EnvironmentVariables();

    @Test
    public void shouldHaveDefaultSettings() throws Exception {

        HoverflyConfiguration configs = configs().build();

        assertThat(configs.getHost()).isEqualTo("localhost");
        assertThat(configs.getScheme()).isEqualTo("http");
        assertThat(configs.isWebServer()).isFalse();
        assertThat(configs.getAdminPort()).isGreaterThan(0);
        assertThat(configs.getProxyPort()).isGreaterThan(0);
        assertThat(configs.getSslCertificatePath()).isNull();
        assertThat(configs.getSslKeyPath()).isNull();

        assertThat(configs.isRemoteInstance()).isFalse();
        assertThat(configs.isProxyLocalHost()).isFalse();
        assertThat(configs.isPlainHttpTunneling()).isFalse();
        assertThat(configs.isWebServer()).isFalse();
        assertThat(configs.isTlsVerificationDisabled()).isFalse();
    }

    @Test
    public void shouldHaveDefaultRemoteSettings() throws Exception {
        HoverflyConfiguration configs = HoverflyConfig.configs().remote().build();

        assertThat(configs.getHost()).isEqualTo("localhost");
        assertThat(configs.getScheme()).isEqualTo("http");
        assertThat(configs.getAdminPort()).isEqualTo(8888);
        assertThat(configs.getProxyPort()).isEqualTo(8500);
        assertThat(configs.getSslCertificatePath()).isNull();
        assertThat(configs.getSslKeyPath()).isNull();

        assertThat(configs.isRemoteInstance()).isTrue();
        assertThat(configs.isProxyLocalHost()).isFalse();
        assertThat(configs.isPlainHttpTunneling()).isFalse();
    }

    @Test
    public void shouldBeAbleToOverrideHostNameByUseRemoteInstance() throws Exception {

        HoverflyConfiguration configs = configs()
                .remote()
                .host("cloud-hoverfly.com")
                .build();

        assertThat(configs.getHost()).isEqualTo("cloud-hoverfly.com");

        assertThat(configs.isRemoteInstance()).isTrue();
    }

    @Test
    public void remoteHoverflyConfigShouldIgnoreCustomSslCertAndKey() throws Exception {
        HoverflyConfiguration configs = configs()
                .sslCertificatePath("ssl/ca.crt")
                .sslKeyPath("ssl/ca.key").remote()
                .remote()
                .build();

        assertThat(configs.getSslCertificatePath()).isNull();
        assertThat(configs.getSslKeyPath()).isNull();

    }

    @Test
    public void shouldSetProxyLocalHost() throws Exception {
        HoverflyConfiguration configs = configs().proxyLocalHost().build();

        assertThat(configs.isProxyLocalHost()).isTrue();
    }

    @Test
    public void shouldSetPlainHttpTunneling() throws Exception {
        HoverflyConfiguration configs = configs().plainHttpTunneling().build();

        assertThat(configs.isPlainHttpTunneling()).isTrue();
    }

    @Test
    public void shouldSetHttpsAdminEndpoint() throws Exception {
        HoverflyConfiguration configs = configs().remote().withHttpsAdminEndpoint().build();

        assertThat(configs.getScheme()).isEqualTo("https");
        assertThat(configs.getAdminPort()).isEqualTo(443);
        assertThat(configs.getAdminCertificate()).isNull();
    }

    @Test
    public void shouldSetAuthTokenFromEnvironmentVariable() throws Exception {

        envVars.set(HoverflyConstants.HOVERFLY_AUTH_TOKEN, "token-from-env");
        HoverflyConfiguration configs = configs().remote().withAuthHeader().build();

        assertThat(configs.getAuthToken()).isPresent();
        configs.getAuthToken().ifPresent(token -> assertThat(token).isEqualTo("token-from-env"));
    }

    @Test
    public void shouldSetAuthTokenDirectly() throws Exception {
        HoverflyConfiguration configs = configs().remote().withAuthHeader("some-token").build();

        assertThat(configs.getAuthToken()).isPresent();
        configs.getAuthToken().ifPresent(token -> assertThat(token).isEqualTo("some-token"));
    }

    @Test
    public void shouldSetCaptureHeaders() throws Exception {
        HoverflyConfiguration configs = configs().captureHeaders("Accept", "Authorization").build();

        assertThat(configs.getCaptureHeaders()).hasSize(2);
        assertThat(configs.getCaptureHeaders()).containsOnly("Accept", "Authorization");
    }

    @Test
    public void shouldSetCaptureOneHeader() throws Exception {
        HoverflyConfiguration configs = configs().captureHeaders("Accept").build();

        assertThat(configs.getCaptureHeaders()).hasSize(1);
        assertThat(configs.getCaptureHeaders()).containsOnly("Accept");
    }

    @Test
    public void shouldSetCaptureAllHeaders() throws Exception {
        HoverflyConfiguration configs = configs().captureAllHeaders().build();

        assertThat(configs.getCaptureHeaders()).hasSize(1);
        assertThat(configs.getCaptureHeaders()).containsOnly("*");
    }

    @Test
    public void shouldSetWebServerMode() throws Exception {
        HoverflyConfiguration configs = configs().asWebServer().build();

        assertThat(configs.isWebServer()).isTrue();
    }

    @Test
    public void shouldDisableTlsVerification() throws Exception {
        HoverflyConfiguration configs = configs().disableTlsVerification().build();

        assertThat(configs.isTlsVerificationDisabled()).isTrue();
    }

}
