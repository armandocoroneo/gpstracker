# GPS Tracker Android

App Android para grabar recorridos GPS, guardarlos y exportarlos a GPX, KML o CSV.

## Caracteristicas
- Graba tu recorrido en tiempo real con el GPS del celular
- Visualiza la ruta en un mapa (OpenStreetMap)
- Guarda tracks en el historial
- Exporta a GPX (Strava, Garmin), KML (Google Earth) o CSV (Excel)

## Como compilar

1. Abre Android Studio
2. Selecciona "Open" y elige esta carpeta
3. Espera a que Gradle sincronice
4. Conecta tu celular o usa un emulador
5. Presiona el boton verde de Play

## Permisos necesarios
- Ubicacion precisa (para leer el GPS)
- Ubicacion en segundo plano (opcional, para grabar con la pantalla apagada)

## Estructura
- TrackerFragment - Pantalla principal con mapa y controles
- HistoryFragment - Lista de tracks guardados
- ExportFragment - Exportacion a GPX/KML/CSV
