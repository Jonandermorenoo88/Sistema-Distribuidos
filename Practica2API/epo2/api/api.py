from flask import Flask, request, jsonify, send_file
import sqlite3

api = Flask(__name__)

def crear_conexion():
    bbdd = sqlite3.connect('bbdd.sqlite')
    bbdd.row_factory = sqlite3.Row  # Resultados como diccionarios
    cursor = bbdd.cursor()

    # Crear tabla si no existe
    cursor.execute(''' 
        CREATE TABLE IF NOT EXISTS paises (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL UNIQUE,
            capital TEXT NOT NULL UNIQUE,
            continente TEXT NOT NULL,
            poblacion INTEGER
        )
    ''')

    # Insertar país por defecto si no existe
    cursor.execute("SELECT COUNT(*) FROM paises WHERE nombre = ?", ('España',))
    if cursor.fetchone()[0] == 0:
        cursor.execute('''
            INSERT INTO paises (nombre, capital, continente, poblacion)
            VALUES (?, ?, ?, ?)
        ''', ('España', 'Madrid', 'Europa', 47000000))
        print("País por defecto 'España' insertado en la BBDD.")

    bbdd.commit()
    return bbdd

@api.route('/')
def presentacion():
    return '''
    API creada para la asignatura sistemas distribuidos - EPO2-1C <br>
    <b>Alumnos:</b> Miguel José Gómez López y Jon Ander Incera Moreno
    '''

@api.route('/paises', methods=['GET'])
def obtener_paises():
    bbdd = crear_conexion()
    cursor = bbdd.cursor()
    cursor.execute("SELECT * FROM paises")
    filas = cursor.fetchall()
    bbdd.close()

    paises = [dict(fila) for fila in filas]
    return jsonify(paises)


@api.route('/paises/<int:id>', methods=['GET'])
def obtener_pais_por_id(id):
    bbdd = crear_conexion()
    cursor = bbdd.cursor()
    cursor.execute("SELECT * FROM paises WHERE id = ?", (id,))
    fila = cursor.fetchone()
    bbdd.close()

    if fila is None:
        return jsonify({"mensaje": "País no encontrado"}), 404

    pais = dict(fila)
    return jsonify(pais), 200


@api.route('/paises', methods=['POST'])
def agregar_pais():
    datos = request.get_json()
    nombre = datos.get('nombre')
    capital = datos.get('capital')
    continente = datos.get('continente')
    poblacion = datos.get('poblacion')

    if not all([nombre, capital, continente, poblacion]):
        return jsonify({"error": "Faltan datos"}), 400

    try:
        bbdd = crear_conexion()
        cursor = bbdd.cursor()
        cursor.execute(
            "INSERT INTO paises (nombre, capital, continente, poblacion) VALUES (?, ?, ?, ?)",
            (nombre, capital, continente, poblacion)
        )
        bbdd.commit()
        bbdd.close()
        return jsonify({"mensaje": "País agregado correctamente"}), 201
    except sqlite3.IntegrityError:
        return jsonify({"error": "Ese país o capital ya existe"}), 409

@api.route('/paises/<int:id>', methods=['DELETE'])
def eliminar_pais(id):
    bbdd = crear_conexion()
    cursor = bbdd.cursor()
    cursor.execute("DELETE FROM paises WHERE id = ?", (id,))
    bbdd.commit()
    bbdd.close()

    if cursor.rowcount == 0:
        return jsonify({"mensaje": "País no encontrado"}), 404
    return jsonify({"mensaje": f"País con ID {id} eliminado correctamente"}), 200


@api.route('/paises/<int:id>', methods=['PUT'])
def actualizar_pais(id):
    datos = request.get_json()
    nombre = datos.get('nombre')
    capital = datos.get('capital')
    continente = datos.get('continente')
    poblacion = datos.get('poblacion')

    if not all([nombre, capital, continente, poblacion]):
        return jsonify({"error": "Faltan datos"}), 400

    try:
        bbdd = crear_conexion()
        cursor = bbdd.cursor()
        cursor.execute('''
            UPDATE paises
            SET nombre = ?, capital = ?, continente = ?, poblacion = ?
            WHERE id = ?
        ''', (nombre, capital, continente, poblacion, id))

        bbdd.commit()
        bbdd.close()

        if cursor.rowcount == 0:
            return jsonify({"mensaje": "País no encontrado"}), 404

        return jsonify({"mensaje": f"País con ID {id} actualizado correctamente"}), 200
    except sqlite3.IntegrityError:
        return jsonify({"error": "Ese país o capital ya existe"}), 409


@api.route('/paises/txt', methods=['GET'])
def obtener_paises_txt():
    bbdd = crear_conexion()
    cursor = bbdd.cursor()
    cursor.execute("SELECT * FROM paises")
    filas = cursor.fetchall()
    bbdd.close()

    if not filas:
        return jsonify({"mensaje": "No hay países en la base de datos"}), 404

    # Crear el archivo .txt con los registros
    archivo_txt = "paises.txt"
    with open(archivo_txt, 'w') as archivo:
        for fila in filas:
            archivo.write(f"ID: {fila['id']}\n")
            archivo.write(f"Nombre: {fila['nombre']}\n")
            archivo.write(f"Capital: {fila['capital']}\n")
            archivo.write(f"Continente: {fila['continente']}\n")
            archivo.write(f"Población: {fila['poblacion']}\n\n")

    # Enviar el archivo como respuesta
    return send_file(archivo_txt, as_attachment=True, download_name="paises.txt", mimetype="text/plain")


if __name__ == '__main__':
    api.run(debug=True, port=4400)
