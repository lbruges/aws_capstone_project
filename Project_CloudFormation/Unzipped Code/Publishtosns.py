import boto3
import json
import os

# Crear clientes de SNS y S3
sns_client = boto3.client('sns')
s3_client = boto3.client('s3')

# Tema SNS (reemplazar con tu ARN)
SNS_TOPIC_ARN = os.environ["snsarn"]

def lambda_handler(event, context):
    # Iterar sobre los eventos de carga
    for record in event['Records']:
        bucket_name = record['s3']['bucket']['name']
        object_key = record['s3']['object']['key']
        file_size = record['s3']['object'].get('size', 'Unknown')
        
        # Generar una URL prefirmada para descargar el archivo
        try:
            presigned_url = s3_client.generate_presigned_url(
                'get_object',
                Params={'Bucket': bucket_name, 'Key': object_key},
                ExpiresIn=3600  # El enlace será válido por 1 hora
            )
        except Exception as e:
            print(f"Error al generar la URL prefirmada: {e}")
            presigned_url = "No se pudo generar la URL prefirmada."
        
        # Mensaje personalizado con el enlace de descarga
        message = f"Un archivo ha sido cargado en el bucket {bucket_name}.\n" \
                  f"Nombre del archivo: {object_key}\n" \
                  f"Tamaño del archivo: {file_size} bytes.\n\n" \
                  f"Enlace de descarga (válido por 1 hora): {presigned_url}"
        
        # Publicar mensaje en SNS
        response = sns_client.publish(
            TopicArn=SNS_TOPIC_ARN,
            Message=message,
            Subject="Notificación de subida a S3"
        )
        
        print(f"Mensaje enviado a SNS: {response['MessageId']}")
    
    return {
        'statusCode': 200,
        'body': json.dumps('Mensaje con enlace de descarga publicado exitosamente.')
    }
