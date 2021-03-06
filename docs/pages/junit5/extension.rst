.. _extension:


Hoverfly Extension
==================

Enabling Hoverfly is as easy as registering the  ``HoverflyExtension`` in your test class:


.. code-block:: java

    @ExtendWith(HoverflyExtension.class)
    class MyTests {
        // ...
    }

Declaring ``HoverflyExtension`` at the top of the class will start Hoverfly in simulation mode with default configurations,
and stop it after all the tests are done.

You can add Hoverfly as an argument to constructor or methods which will be dynamically resolved by JUnit 5 runtime.
It gives you the ability to configure hoverfly in a per-test basis, and use Hoverfly API to simulate using :ref:`dsl`
or perform :ref:`verification`.

.. code-block:: java

    @Test
    void shouldDoSomethingWith(Hoverfly hoverfly) {
        // ...
    }


As you can see, injecting Hoverfly into your test gives you a lot of flexibility, and ``HoverflyExtension`` takes care of
resetting Hoverfly before each test to prevent interference.

Configurations
--------------

You can override Hoverfly configuration via the ``@HoverflyCore`` annotation, for example to change the ports of Hoverfly:

.. code-block:: java

    @HoverflyCore(mode = HoverflyMode.CAPTURE, config = @HoverflyConfig(adminPort = 9000, proxyPort = 9001))
    @ExtendWith(HoverflyExtension.class)
    class CustomHoverflyTests {
        // ...
    }

All the existing Hoverfly :ref:`configuration` options are available via the ``@HoverflyConfig`` interface.

Simulate
--------

With ``@HoverflySimulate`` annotation, you can declare a global simulation source that applies to all the tests in a test class.
If one test loads a different simulation, ``HoverflyExtension`` is able to import the global source declared in ``@HoverflySimulate`` before
the next test run.

.. code-block:: java

    package com.x.y.z;

    @HoverflySimulate(source = @HoverflySimulate.Source(value = "test-service-https.json", type = HoverflySimulate.SourceType.CLASSPATH))
    @ExtendWith(HoverflyExtension.class)
    class SimulationTests {
        // ...
    }

The current supported source type is ``CLASSPATH``, ``FILE``, and ``DEFAULT_PATH``(which is ``src/test/resources/hoverfly``)

If no source is provided, it will try to locate a file called with fully qualified name of test class, replacing dots (.) and dollar signs ($) to underlines (_) in the Hoverfly default path.
In this example, the file path that will be looked for is ``src/test/resources/hoverfly/com_x_y_z_SimulationTests.json``


Capture
-------

You can declare ``@HoverflyCapture`` to run Hoverfly in capture mode (see :ref:`capturing`). You can customize the path and the filename for exporting the simulations.

.. code-block:: java

    @HoverflyCapture(path = "build/resources/test/hoverfly",
                filename = "captured-simulation.json",
                config = @HoverflyConfig(captureAllHeaders = true, proxyLocalHost = true))
    @ExtendWith(HoverflyExtension.class)
    class CaptureTests {
        // ...
    }

If ``path`` and ``filename`` are not provided, the simulation will be exported to a file with fully-qualified name of the test class in the default Hoverfly path.

Capture or simulate
-------------------

You can set ``HoverflyExtension`` to switch between simulate and capture mode automatically. If a source is not found, it will capture, otherwise, simulate.
This is previously known as ``inCaptureOrSimulateMode`` in JUnit 4 ``HoverflyRule`` (see :ref:`captureorsimulate`).

This feature can be enabled easily by setting ``enableAutoCapture`` to ``true`` in ``@HoverflySimulate``:

.. code-block:: java

    @HoverflySimulate(source = @Source(value = "build/resources/test/hoverfly/missing-simulation.json", type = SourceType.FILE),
        enableAutoCapture = true)
    @ExtendWith(HoverflyExtension.class)
    class CaptureIfFileNotPresent {
        // ...
    }


Nested tests
------------

If your test class contains several groups of tests that require different Hoverfly configurations, you can do so by registering
``HoverflyExtension`` with nested tests:

.. code-block:: java

    @Nested
    @HoverflySimulate
    @ExtendWith(HoverflyExtension.class)
    class MyNestedTestsOne {
        // ...
    }

    @Nested
    @HoverflyCapture
    @ExtendWith(HoverflyExtension.class)
    class MyNestedTestsTwo {
        // ...
    }