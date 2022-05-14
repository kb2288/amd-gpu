module hp.community.gpu.app.ui {
  requires hp.community.gpu.app.service;
  requires java.desktop;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.fxml;
  requires org.slf4j;

  requires hp.community.gpu.app.core;

  requires aparapi;
  requires aparapi.jni;

  opens hp.community.gpu.app.ui to
      javafx.fxml;

  uses hp.community.gpu.app.service.GpuTask;

  exports hp.community.gpu.app.ui;
}
