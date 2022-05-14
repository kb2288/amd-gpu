package hp.community.gpu.app.core;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import hp.community.gpu.app.service.GpuTask;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
public class DefaultGpuTask implements GpuTask {

  // AMD RX6600m is basically AMD NAVI 34 processor
  private static final String DEFAULT_GPU = "gfx1032";

  // Will select the preferred device for the kernelManager()
  private static final OpenCLDevice preferredDevice = selectPreferredDevice();

  // Default range size
  private static final int MAX_RANGE = 4096;
  private static final String TASK_NAME = "Default";

  private boolean isRunning = false;

  // Run the task
  public void start() {
    new Thread(this::runTask).start();
  }

  public void stop() {
    this.isRunning = false;
  }

  @Override
  public String getTaskName() {
    return TASK_NAME;
  }

  // Task itself
  public void runTask() {

    log.info("The GPU task is started!");

    isRunning = true;

    // Two arrays
    final double[] in1Copy = new double[MAX_RANGE];
    final double[] in2Copy = new double[MAX_RANGE];

    // The result of the simple calculations
    final double[] result = new double[1];

    // Create the kernel
    final Kernel kernel =
        new Kernel() {
          @Override
          public void run() {
            int i = getGlobalId();
            result[0] += in1Copy[i] + in2Copy[i];
          }
        };

    // Creating a range based on the arrays
    final Range range = Range.create(in1Copy.length);

    while (isRunning) {
      // Fill the arrays with some dummy values
      result[0] = 0;
      for (int i = 0; i < MAX_RANGE; i++) {
        in1Copy[i] = i;
        in2Copy[i] = i * 10.0;
      }

      kernel.execute(range);
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    kernel.dispose();

    log.info("The GPU task is stopped!");
  }

  private static OpenCLDevice selectPreferredDevice() {
    final List<OpenCLDevice> openCLDeviceList = OpenCLDevice.listDevices(Device.TYPE.GPU);
    OpenCLDevice externalGPU = null;
    for (OpenCLDevice device : openCLDeviceList) {
      log.info("------------------------------------");
      log.info("Device Name: " + device.getName());
      log.info("Device: " + device);
      if (DEFAULT_GPU.equals(device.getName())) {
        externalGPU = device;
      }
    }
    log.info("------------------------------------");
    if (externalGPU != null) {
      log.info("Selected DeviceID: " + externalGPU.getDeviceId());
      KernelManager.instance()
          .setDefaultPreferredDevices(new LinkedHashSet<>(List.of(externalGPU)));
    } else {
      log.info("No device is selected!");
    }
    log.info("------------------------------------");
    return externalGPU;
  }
}
