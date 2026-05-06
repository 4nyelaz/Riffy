/* ── navegación sidebar ── */
const titulosSecciones = {
    usuarios:       ['Usuarios',       'Gestiona los usuarios de la plataforma'],
    productos:      ['Productos',      'Modera los productos publicados'],
    conversaciones: ['Conversaciones', 'Supervisa los hilos entre usuarios']
};

document.addEventListener('DOMContentLoaded', function () {

    const links = document.querySelectorAll('[data-seccion]');
    const secciones = document.querySelectorAll('.seccion');

    links.forEach(function (link) {
        link.addEventListener('click', function () {

            const nombre = this.dataset.seccion;

            // cambiar sección visible
            secciones.forEach(s => s.classList.remove('activa'));
            document.getElementById('seccion-' + nombre).classList.add('activa');

            // cambiar activo sidebar
            links.forEach(n => n.classList.remove('active'));
            this.classList.add('active');

            const header = document.querySelector('#seccion-' + nombre + ' .card-header');
            if (header) {
                header.innerHTML = `<i class="fas fa-circle text-danger me-2"></i> ${titulosSecciones[nombre][0]}`;
            }

        });
    });
});


/* ── toggle conversación ── */
function toggleConversacion(id) {
    $.ajax({
        url: '/admin/api/conversaciones/' + id + '/estado',
        type: 'PATCH',
        success: function () {
            $('#tabla-conversaciones').jtable('reload');
        },
        error: function () {
            alert('Error al cambiar el estado de la conversación.');
        }
    });
}


/* ── jTable init ── */
$(document).ready(function () {

    /* usuarios */
    $('#tabla-usuarios').jtable({
        title: 'Usuarios',
        paging: true,
        pageSize: 10,
        sorting: true,
        defaultSorting: 'idUsuario ASC',
        actions: {
            listAction: '/admin/api/usuarios',
            deleteAction: '/admin/api/usuarios/{id}'
        },
        fields: {
            idUsuario: {
                title: 'ID',
                width: '5%',
                key: true,
                list: true,
                edit: false,
                create: false
            },
            nombre: { title: 'Nombre', width: '22%' },
            usuario: { title: 'Usuario', width: '20%' },
            fechaRegistro: {
                title: 'Registro',
                width: '16%',
                list: true,
                edit: false,
                create: false
            },
            rol: {
                title: 'Rol',
                width: '12%',
                options: { 'USER': 'USER', 'ADMIN': 'ADMIN' },
                display: function (data) {
                    var clase = data.record.rol === 'ADMIN' ? 'badge-admin' : 'badge-user';
                    return '<span class="badge ' + clase + '">' + data.record.rol + '</span>';
                }
            }
        }
    });
    
    /* productos */
    $('#tabla-productos').jtable({
        title: 'Productos',
        paging: true,
        pageSize: 10,
        sorting: true,
        defaultSorting: 'id_producto ASC',
        actions: {
            listAction: '/admin/api/productos',
            deleteAction: '/admin/api/productos/{id}'
        },
        fields: {
            id_producto: {
                title: 'ID',
                width: '5%',
                key: true,
                list: true,
                edit: false,
                create: false
            },
            titulo: { title: 'Título', width: '24%' },
            artista: { title: 'Artista', width: '16%' },
            formato: { title: 'Formato', width: '10%' },
            precio: {
                title: 'Precio',
                width: '10%',
                display: function (data) {
                    return '<strong>' + data.record.precio + ' €</strong>';
                }
            },
            estado: {
                title: 'Estado',
                width: '12%',
                display: function (data) {
                    var e = data.record.estado;
                    var clase = e === 'Disponible' ? 'badge-disponible' : 'badge-vendido';
                    return '<span class="badge ' + clase + '">' + e + '</span>';
                }
            },
            categoria: { title: 'Categoría', width: '12%' }
        }
    });
    


    /* conversaciones */
    $('#tabla-conversaciones').jtable({
        title: 'Conversaciones',
        paging: true,
        pageSize: 10,
        sorting: true,
        defaultSorting: 'id_conversacion ASC',
        actions: {
            listAction: '/admin/api/conversaciones'
        },
        fields: {
            id_conversacion: {
                title: 'ID',
                width: '5%',
                key: true,
                list: true,
                edit: false,
                create: false
            },
            'producto.titulo': {
                title: 'Producto',
                width: '22%',
                display: function (data) {
                    return data.record.producto ? data.record.producto.titulo : '-';
                }
            },
            'comprador.usuario': {
                title: 'Comprador',
                width: '16%',
                display: function (data) {
                    return data.record.comprador ? '@' + data.record.comprador.usuario : '-';
                }
            },
            'vendedor.usuario': {
                title: 'Vendedor',
                width: '16%',
                display: function (data) {
                    return data.record.vendedor ? '@' + data.record.vendedor.usuario : '-';
                }
            },
            fechaCreacion: { title: 'Fecha', width: '13%' },
            conversacionActiva: {
                title: 'Estado',
                width: '10%',
                display: function (data) {
                    var activa = data.record.conversacionActiva;
                    var clase = activa ? 'badge-activa' : 'badge-cerrada';
                    var texto = activa ? 'Activa' : 'Cerrada';
                    return '<span class="badge ' + clase + '">' + texto + '</span>';
                }
            },
            acciones: {
                title: 'Acciones',
                width: '10%',
                sorting: false,
                display: function (data) {
                    var id = data.record.id_conversacion;
                    var texto = data.record.conversacionActiva ? 'Cerrar' : 'Reabrir';
                    return `<button class="btn btn-sm btn-outline-primary" onclick="toggleConversacion(${id})">${texto}</button>`;
                }
            }
        }
    });
    

    /* 2. AQUÍ ES DONDE VA */
    if ($.fn.jtable) {
        $('#tabla-usuarios').jtable('load');
        $('#tabla-productos').jtable('load');
        $('#tabla-conversaciones').jtable('load');
    } else {
        console.error("jTable no está cargado");
    }

});