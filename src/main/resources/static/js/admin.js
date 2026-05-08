"use strict";
/**
 * Panel de Administración - Riffy
 * Controla las pestañas y la navegación del panel
 */

// ── lógica de pestañas ──
function mostrarPanel(seccion, tabEl) {
    // ocultar todos los paneles
    document.querySelectorAll('.admin-panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));

    // mostrar el elegido
    const panel = document.getElementById('panel-' + seccion);
    if (panel) {
        panel.classList.add('active');
    }
    if (tabEl) {
        tabEl.classList.add('active');
    }
}

// ── activar pestaña correcta según param ?seccion= al cargar ──
function activarPestanaPorURL() {
    const params = new URLSearchParams(window.location.search);
    const seccion = params.get('seccion') || 'usuarios';
    
    let tab = null;
    if (seccion === 'usuarios') {
        tab = document.querySelector('.admin-tab[onclick*="usuarios"]');
    } else if (seccion === 'productos') {
        tab = document.querySelector('.admin-tab[onclick*="productos"]');
    } else if (seccion === 'conversaciones') {
        tab = document.querySelector('.admin-tab[onclick*="conversaciones"]');
    }
    
    if (tab) {
        document.querySelectorAll('.admin-panel').forEach(p => p.classList.remove('active'));
        document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
        
        const panel = document.getElementById('panel-' + seccion);
        if (panel) {
            panel.classList.add('active');
        }
        tab.classList.add('active');
    }
}

// ── confirmación para eliminar (mejora la experiencia) ──
function initConfirmaciones() {
    // Usuarios
    document.querySelectorAll('form[action*="/eliminar"]').forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('¿Estás seguro? Esta acción no tiene vuelta atrás.')) {
                e.preventDefault();
            }
        });
    });
}

// ── inicializar cuando el DOM esté listo ──
document.addEventListener('DOMContentLoaded', function() {
    activarPestanaPorURL();
    initConfirmaciones();
});