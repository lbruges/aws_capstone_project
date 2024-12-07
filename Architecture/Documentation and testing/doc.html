
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Swagger UI</title>
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:400,700|Source+Code+Pro:300,600|Titillium+Web:400,600,700" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/3.24.2/swagger-ui.css" >
  <style>
    html
    {
      box-sizing: border-box;
      overflow: -moz-scrollbars-vertical;
      overflow-y: scroll;
    }
    *,
    *:before,
    *:after
    {
      box-sizing: inherit;
    }

    body {
      margin:0;
      background: #fafafa;
    }
  </style>
</head>
<body>

<div id="swagger-ui"></div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/3.24.2/swagger-ui-bundle.js"> </script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/3.24.2/swagger-ui-standalone-preset.js"> </script>
<script>
window.onload = function() {

  var spec = {"openapi": "3.0.3", "info": {"title": "Utilities API", "description": "Documentaci&oacute;n de la API para obtenci&oacute;n de datos de consumo, producci&oacute;n y generaci&oacute;n de reportes", "version": "1.0.0", "contact": {"name": "Immune API Support", "email": "laura.bruges.cco@immune.institute", "url": "https://github.com/lbruges/aws_capstone_project"}}, "servers": [{"url": "{{api_gw}}", "description": "API Gateway"}], "paths": {"/api/production": {"post": {"summary": "Crear o actualizar datos de producción", "operationId": "updateProduction", "requestBody": {"content": {"application/json": {"schema": {"type": "object", "properties": {"date": {"type": "string", "example": "12-2024", "description": "Fecha en formato mes-a&ntilde;o"}, "zone": {"type": "string", "example": "C", "description": "Identificador de zona"}, "costPerM3": {"type": "number", "format": "float", "example": 19, "description": "Consumo por metro c&uacute;bico"}}}}}}, "responses": {"200": {"description": "Los datos de producci&oacute;n se han actualizado correctamente"}, "400": {"description": "El formato del payload es incorrecto o no se han inclu&iacute;do datos de la fecha, zona o producci&oacute;n"}, "500": {"description": "Error inesperado (por ejemplo, la tabla de producci&oacute;n no existe o no hay conexi&oacute;n con la base de datos)"}}}}, "/api/production/{zone}/{date}": {"get": {"summary": "Obtener costos de producción por zona y fecha", "operationId": "getProductionCosts", "parameters": [{"in": "path", "name": "zone", "required": true, "schema": {"type": "string", "example": "A", "description": "Identificador de zona"}}, {"in": "path", "name": "date", "required": true, "schema": {"type": "string", "example": "12-2024", "description": "Fecha en formato mes-a&ntilde;o"}}], "responses": {"200": {"description": "Datos de costo de producci&oacute;n por metro c&uacute;bico por mes y zona"}, "400": {"description": "El formato de la ruta es incorrecto o no se han inclu&iacute;do datos de la fecha y/o zona"}, "500": {"description": "Error inesperado (por ejemplo, la tabla de producci&oacute;n no existe o no hay conexi&oacute;n con la base de datos)"}}}}, "/api/consumption/{zone}/{date}": {"get": {"summary": "Obtener datos de consumo promedio por zona y fecha", "operationId": "getConsumption", "parameters": [{"in": "path", "name": "zone", "required": true, "schema": {"type": "string", "example": "A", "description": "Identificador de zona"}}, {"in": "path", "name": "date", "required": true, "schema": {"type": "string", "example": "12-2024", "description": "Fecha en formato mes-a&ntilde;o"}}], "responses": {"200": {"description": "Datos de consumo por zona por mes"}, "400": {"description": "No se han prove&iacute;do datos de zona o fecha"}, "500": {"description": "Error inesperado (por ejemplo, la tabla de consumos no existe o no hay conexi&oacute;n con la base de datos)"}}}}, "/api/report": {"put": {"summary": "Generar uno o múltiples reportes y actualizar la base de datos de utilidades", "operationId": "generateReport", "requestBody": {"content": {"application/json": {"schema": {"type": "object", "properties": {"SQSEvent": {"type": "array", "items": {"type": "object", "properties": {"zone": {"type": "string", "example": "B", "description": "Identificador de zona"}, "date": {"type": "string", "example": "12-2024", "description": "Fecha en formato mes-a&ntilde;o"}}}}}}}}}, "responses": {"200": {"description": "Se ha encolado una solicitud de generaci&oacute;n de reporte(s)"}, "500": {"description": "Ha ocurrido un error inesperado"}}}}, "/api/report/{zone}/{date}": {"get": {"summary": "Descargar reportes", "operationId": "downloadReport", "parameters": [{"in": "path", "name": "zone", "required": true, "schema": {"type": "string", "example": "A", "description": "Identificador de zona"}}, {"in": "path", "name": "date", "required": true, "schema": {"type": "string", "example": "12-2024", "description": "Fecha en formato mes-a&ntilde;o"}}], "responses": {"200": {"description": "Reporte generado en pdf correspondiente a las utilidades para el mes y zona prove&iacute;dos"}, "400": {"description": "La fecha o zona no han sido prove&iacute;dos"}}}}}};

  // Build a system
  const ui = SwaggerUIBundle({
    spec: spec,
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  })

  window.ui = ui
}
</script>
</body>

</html>
