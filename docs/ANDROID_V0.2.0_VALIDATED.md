# STYLO DSP Android v0.2.0 — Validated Milestone

## Estado

**VALIDADO EN DISPOSITIVO FÍSICO**

El APK generado desde la rama `prototype/android-apk` fue instalado y probado en Android. La aplicación abre correctamente después de corregir la dependencia ELF que apuntaba a una ruta absoluta del runner de GitHub Actions.

## Arquitectura validada

```text
Android
  ↓
Oboe / AAudio
  ↓
Native bridge / FFI
  ↓
STYLO DSP Core (Rust)
  ↓
Gain (f32)
```

## Incidencia corregida

El APK inicial cerraba al arrancar con `java.lang.UnsatisfiedLinkError` porque `libstylo_android.so` declaraba como dependencia una ruta absoluta del entorno de CI hacia `libstylo_dsp_core.so`.

La corrección estableció:

```text
SONAME = libstylo_dsp_core.so
DT_NEEDED = libstylo_dsp_core.so
```

Y CI verifica que las dependencias ELF no contienen rutas del runner como `/home/runner/...`.

## Build de referencia

- Rama de implementación: `prototype/android-apk`
- Rama de referencia: `android-v0.2.0-validated`
- Commit de build validado: `85f4bdbf6030cbc3d83903874c0e01e033bb80fb`
- GitHub Actions run: `31965884079`
- Arquitectura Android: `arm64-v8a`

## Contenido nativo esperado

```text
lib/arm64-v8a/
├── libc++_shared.so
├── liboboe.so
├── libstylo_dsp_core.so
└── libstylo_android.so
```

## Próximo gate

El siguiente desarrollo debe partir de esta referencia estable y añadir, en este orden:

1. Parámetros lock-free UI → DSP.
2. Gain configurable en tiempo real.
3. Política zero-allocation durante el callback.
4. Metering mediante snapshots a 30/60 FPS.
5. Pruebas prolongadas de estabilidad.
6. Posterior migración de módulos DSP.

No se debe alterar esta rama de referencia para experimentos. Las nuevas funcionalidades deben desarrollarse en ramas derivadas.
