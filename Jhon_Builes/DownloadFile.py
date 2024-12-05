import json
import boto3
import urllib.request
import os


s3_client = boto3.client("s3")
bucket_name = os.environ["bucket"]

def lambda_handler(event, context):
    try:
        # Extraer el nombre del archivo desde el parámetro de entrada
        zone = event['queryStringParameters']['zone']
        date = event['queryStringParameters']['date']
        object_name = f'{zone}_{date}.pdf'
        
        # Genera una URL prefirmada para el archivo
        file_url = s3_client.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucket_name, 'Key': object_name},
            ExpiresIn=60 # La URL será válida durante 1 hora
        )
        
        # Descargamos el archivo usando la URL prefirmada
        with urllib.request.urlopen(file_url) as response:
            file_data = response.read()  # Lee el contenido del archivo
        
        # Establecemos los encabezados para indicar la descarga
        return {
            "statusCode": 200,
            "headers": {
                "Content-Type": "application/octet-stream",
                "Content-Disposition": f"attachment; filename={object_name}",
            },
            "body":file_data,  # El cuerpo contendrá el archivo
              # Indicamos que el contenido no está en base64
        }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error: {str(e)}')
        }
