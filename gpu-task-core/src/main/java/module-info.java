module hp.community.gpu.app.core {
  requires lombok;
  requires aparapi;
  requires aparapi.jni;
  requires org.slf4j;
  requires hp.community.gpu.app.service;

  provides hp.community.gpu.app.service.GpuTask with
      hp.community.gpu.app.core.DefaultGpuTask;

  exports hp.community.gpu.app.core;

  opens hp.community.gpu.app.core to
      aparapi;
}
