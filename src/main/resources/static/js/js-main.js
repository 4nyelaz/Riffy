"use strict";

// Función para abrir el acordeón según el hash actual
    function abrirAcordeonDesdeHash() {
        const hash = window.location.hash; // Ej: "#seccion-compraventa"
        
        if (hash) {
            const accordionItem = document.querySelector(hash);
            
            if (accordionItem) {
                const button = accordionItem.querySelector('.accordion-button');
                const collapseElement = accordionItem.querySelector('.accordion-collapse');
                
                if (button && collapseElement) {
                    // Primero cerramos todos los acordeones (opcional, para limpiar)
                    document.querySelectorAll('.accordion-collapse').forEach(el => {
                        const bsCollapse = bootstrap.Collapse.getInstance(el);
                        if (bsCollapse) bsCollapse.hide();
                    });
                    
                    // Abrimos el que nos interesa
                    const bsCollapse = new bootstrap.Collapse(collapseElement, {
                        toggle: false // No toggle, queremos asegurar que se abra
                    });
                    bsCollapse.show();
                    
                    // Scroll suave hasta la pregunta
                    setTimeout(() => {
                        accordionItem.scrollIntoView({ 
                            behavior: 'smooth', 
                            block: 'center' 
                        });
                    }, 300);
                }
            }
        }
    }

    // 1. Ejecutar al cargar la página
    document.addEventListener('DOMContentLoaded', abrirAcordeonDesdeHash);
    
    // 2. Ejecutar cuando cambie el hash (CLAVE para que funcione sin recargar)
    window.addEventListener('hashchange', abrirAcordeonDesdeHash);

// LOGIN

document.addEventListener('DOMContentLoaded', function () {

    const pestanaLogin = document.getElementById('pestana-login');
    const pestanaRegistro = document.getElementById('pestana-registro');
    const secLogin = document.getElementById('sec-login');
    const secRegistro = document.getElementById('sec-registro');
    const enlaceRegistro = document.getElementById('enlace-registro');
    const enlaceLogin = document.getElementById('enlace-login');

    function cambiarSeccion(seccion) {
        const esLogin = seccion === 'login';

        secLogin.classList.toggle('activa', esLogin);
        secRegistro.classList.toggle('activa', !esLogin);
        pestanaLogin.classList.toggle('activa', esLogin);
        pestanaRegistro.classList.toggle('activa', !esLogin);

        history.replaceState(null, '', window.location.pathname);
    }

    pestanaLogin.addEventListener('click', function () {
        cambiarSeccion('login');
    });

    pestanaRegistro.addEventListener('click', function () {
        cambiarSeccion('registro');
    });

    enlaceRegistro.addEventListener('click', function () {
        cambiarSeccion('registro');
    });

    enlaceLogin.addEventListener('click', function () {
        cambiarSeccion('login');
    });

    const params = new URLSearchParams(window.location.search);
    if (params.get('section') === 'register') cambiarSeccion('registro');
    else cambiarSeccion('login');

});
