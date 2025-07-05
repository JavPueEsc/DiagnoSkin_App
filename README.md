 
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

👉 Enlace de descarga de la API REST de la aplicación. Si se usa en localhost con XAMPP, colocar en la ruta C:\xampp\htdocs\ApiRestDiagnoSkin
https://drive.google.com/uc?id=1lP74z2jNM48mx9X_feIYpIo55G3aRY5m

👉 Acceso al código fuente del modelo de IA en Python.
https://github.com/JavPueEsc/DiagnoSkin_App_ModeloIA.git
