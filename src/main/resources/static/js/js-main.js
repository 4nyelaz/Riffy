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
