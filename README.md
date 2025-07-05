 
👉 Configuración de Google Maps API
Para que la funcionalidad del mapa —específicamente la selección de centros de salud— funcione correctamente en la app, debe generar una clave de API de Google Maps vinculada a su proyecto. Sigua estos pasos:

Acceda a la consola de Google Cloud:
 https://console.cloud.google.com/apis/credentials

Cree una nueva clave de API para Google Maps SDK for Android.

Vincule esa clave con:

El nombre del paquete de su aplicación (por ejemplo, com.suempresa.tuapp).

El SHA-1 del certificado de firma de su app.
(Puede obtenerlo desde Android Studio o con el comando keytool).

Una vez creada y configurada la clave, añádala en el archivo res/values/strings.xml de su proyecto, dentro de esta etiqueta:
```xml
<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">AQUÍ_SU_CLAVE_API</string>

