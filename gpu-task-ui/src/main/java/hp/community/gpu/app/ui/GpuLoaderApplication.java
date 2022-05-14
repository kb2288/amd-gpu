package hp.community.gpu.app.ui;

import hp.community.gpu.app.service.GpuTask;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Slf4j
public class GpuLoaderApplication extends Application {

  private final Menu gpuTaskMenu = new Menu("GPU Task");
  private final MenuItem exitItem = new MenuItem("Exit");

  private final Map<String, TaskItem> taskItems = new HashMap<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {

    // Get default task to autostart
    final String defaultTask = System.getProperty("task", "Default");

    log.info("Default task name is: {}", defaultTask);

    // Hide the stage at startup
    stage.hide();

    // Check the SystemTray is supported
    if (!SystemTray.isSupported()) {
      log.error("SystemTray is not supported!");
      return;
    }

    // Create popup menu
    final PopupMenu popup = new PopupMenu();

    // Add the icon to the application tray
    final URL url = GpuLoaderApplication.class.getResource("/icon/gpu_tiny.png");
    final Image image = Toolkit.getDefaultToolkit().getImage(url);
    final TrayIcon trayIcon = new TrayIcon(image);
    final SystemTray tray = SystemTray.getSystemTray();

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      log.error("TrayIcon could not be added.");
    }

    // Build menu item per service
    ServiceLoader.load(GpuTask.class).forEach(this::createTaskMenuItem);

    // Add components to pop-up menu (menus/items)
    popup.add(gpuTaskMenu);
    popup.add(exitItem);
    trayIcon.setPopupMenu(popup);

    // Exit
    exitItem.addActionListener(e -> System.exit(0));

    // Start on startup
    start(defaultTask);
  }

  private void createTaskMenuItem(GpuTask task) {

    // Cache the taskItem
    final String itemName = task.getTaskName();
    final MenuItem startItem = new MenuItem("Start");
    final MenuItem stopItem = new MenuItem("Stop");
    final TaskItem taskItem = new TaskItem(itemName, stopItem, startItem, task);
    taskItems.put(itemName, taskItem);

    // Add menu items
    final Menu taskMenu = new Menu(itemName);
    taskMenu.add(startItem);
    taskMenu.add(stopItem);
    gpuTaskMenu.add(taskMenu);

    // Add action listeners
    startItem.addActionListener(e -> start(task.getTaskName()));
    stopItem.addActionListener(e -> stop(task.getTaskName()));
  }

  public void start(String taskName) {
    final TaskItem taskItem = taskItems.get(taskName);
    if (taskItem == null) {
      log.error("Task {} is not found.", taskName);
      throw new IllegalArgumentException("Incorrect taskName specified " + taskName);
    }
    taskItem.startItem().setEnabled(false);
    taskItem.stopItem().setEnabled(true);
    taskItem.gpuTask().start();
  }

  public void stop(String taskName) {
    final TaskItem taskItem = taskItems.get(taskName);
    if (taskItem == null) {
      log.error("Task {} is not found.", taskName);
      throw new IllegalArgumentException("Incorrect taskName specified " + taskName);
    }
    taskItem.startItem().setEnabled(true);
    taskItem.stopItem().setEnabled(false);
    taskItem.gpuTask().stop();
  }

  private record TaskItem(
      String taskName, MenuItem stopItem, MenuItem startItem, GpuTask gpuTask) {}
}
