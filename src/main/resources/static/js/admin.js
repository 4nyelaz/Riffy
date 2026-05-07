// lee el parámetro ?seccion= de la URL para volver a la pestaña correcta tras una acción
document.addEventListener('DOMContentLoaded', function () {

    const botones  = document.querySelectorAll('[data-seccion]');
    const secciones = document.querySelectorAll('.seccion');

    function activar(nombre) {
        secciones.forEach(s => s.classList.remove('activa'));
        botones.forEach(b => b.classList.remove('active'));

        const seccion = document.getElementById('seccion-' + nombre);
        const boton   = document.querySelector('[data-seccion="' + nombre + '"]');

        if (seccion) seccion.classList.add('activa');
        if (boton)   boton.classList.add('active');
    }

    // si el controller redirige con ?seccion=productos, abre esa pestaña directamente
    const params = new URLSearchParams(window.location.search);
    const seccionParam = params.get('seccion');
    if (seccionParam) activar(seccionParam);

    // clicks normales
    botones.forEach(b => {
        b.addEventListener('click', () => activar(b.dataset.seccion));
    });

});