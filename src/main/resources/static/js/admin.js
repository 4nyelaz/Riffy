"use strict";
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

/* ------------------------------------------------------------------ */
/* dialog — eliminar usuario (admin)
/* ------------------------------------------------------------------ */

const dialogEliminarUsuario = document.createElement('dialog');
dialogEliminarUsuario.id = 'dialogEliminarUsuario';

const boxEliminarUsuario = document.createElement('div');
boxEliminarUsuario.className = 'dialog-box';

const h5EliminarUsuario = document.createElement('h5');
h5EliminarUsuario.textContent = '¿Eliminar usuario?';

const pEliminarUsuario = document.createElement('p');
pEliminarUsuario.id = 'dialogEliminarUsuarioNombre';

const actionsEliminarUsuario = document.createElement('div');
actionsEliminarUsuario.className = 'dialog-actions';

const btnConfirmarEliminarUsuario = document.createElement('button');
btnConfirmarEliminarUsuario.id = 'btnConfirmarEliminarUsuario';
btnConfirmarEliminarUsuario.className = 'btn btn-danger btn-sm rounded-pill';
btnConfirmarEliminarUsuario.textContent = 'Confirmar';

const btnCancelarEliminarUsuario = document.createElement('button');
btnCancelarEliminarUsuario.id = 'btnCancelarEliminarUsuario';
btnCancelarEliminarUsuario.className = 'btn btn-secondary btn-sm rounded-pill';
btnCancelarEliminarUsuario.textContent = 'Cancelar';

actionsEliminarUsuario.appendChild(btnConfirmarEliminarUsuario);
actionsEliminarUsuario.appendChild(btnCancelarEliminarUsuario);
boxEliminarUsuario.appendChild(h5EliminarUsuario);
boxEliminarUsuario.appendChild(pEliminarUsuario);
boxEliminarUsuario.appendChild(actionsEliminarUsuario);
dialogEliminarUsuario.appendChild(boxEliminarUsuario);
document.body.appendChild(dialogEliminarUsuario);

/* ------------------------------------------------------------------ */
/* dialog — eliminar producto (admin)
/* ------------------------------------------------------------------ */

const dialogEliminarProductoAdmin = document.createElement('dialog');
dialogEliminarProductoAdmin.id = 'dialogEliminarProductoAdmin';

const boxEliminarProductoAdmin = document.createElement('div');
boxEliminarProductoAdmin.className = 'dialog-box';

const h5EliminarProductoAdmin = document.createElement('h5');
h5EliminarProductoAdmin.textContent = '¿Eliminar producto?';

const pEliminarProductoAdmin = document.createElement('p');
pEliminarProductoAdmin.id = 'dialogEliminarProductoAdminNombre';

const actionsEliminarProductoAdmin = document.createElement('div');
actionsEliminarProductoAdmin.className = 'dialog-actions';

const btnConfirmarEliminarProductoAdmin = document.createElement('button');
btnConfirmarEliminarProductoAdmin.id = 'btnConfirmarEliminarProductoAdmin';
btnConfirmarEliminarProductoAdmin.className = 'btn btn-danger btn-sm rounded-pill';
btnConfirmarEliminarProductoAdmin.textContent = 'Confirmar';

const btnCancelarEliminarProductoAdmin = document.createElement('button');
btnCancelarEliminarProductoAdmin.id = 'btnCancelarEliminarProductoAdmin';
btnCancelarEliminarProductoAdmin.className = 'btn btn-secondary btn-sm rounded-pill';
btnCancelarEliminarProductoAdmin.textContent = 'Cancelar';

actionsEliminarProductoAdmin.appendChild(btnConfirmarEliminarProductoAdmin);
actionsEliminarProductoAdmin.appendChild(btnCancelarEliminarProductoAdmin);
boxEliminarProductoAdmin.appendChild(h5EliminarProductoAdmin);
boxEliminarProductoAdmin.appendChild(pEliminarProductoAdmin);
boxEliminarProductoAdmin.appendChild(actionsEliminarProductoAdmin);
dialogEliminarProductoAdmin.appendChild(boxEliminarProductoAdmin);
document.body.appendChild(dialogEliminarProductoAdmin);

/* ------------------------------------------------------------------ */
/* lógica — interceptar botones de eliminar en admin
/* ------------------------------------------------------------------ */

let formPendienteAdmin = null;

document.addEventListener('click', function (e) {
  const btnU = e.target.closest('[data-admin-eliminar-usuario]');
  if (btnU) {
    e.preventDefault();
    formPendienteAdmin = btnU.closest('form');
    document.getElementById('dialogEliminarUsuarioNombre').textContent =
      '¿Seguro que quieres eliminar al usuario "' + btnU.dataset.adminEliminarUsuario + '"?';
    dialogEliminarUsuario.showModal();
    return;
  }

  const btnP = e.target.closest('[data-admin-eliminar-producto]');
  if (btnP) {
    e.preventDefault();
    formPendienteAdmin = btnP.closest('form');
    document.getElementById('dialogEliminarProductoAdminNombre').textContent =
      '¿Seguro que quieres eliminar el producto "' + btnP.dataset.adminEliminarProducto + '"?';
    dialogEliminarProductoAdmin.showModal();
    return;
  }
});

btnConfirmarEliminarUsuario.addEventListener('click', () => {
  dialogEliminarUsuario.close();
  if (formPendienteAdmin) { formPendienteAdmin.submit(); formPendienteAdmin = null; }
});
btnCancelarEliminarUsuario.addEventListener('click', () => {
  dialogEliminarUsuario.close();
  formPendienteAdmin = null;
});

btnConfirmarEliminarProductoAdmin.addEventListener('click', () => {
  dialogEliminarProductoAdmin.close();
  if (formPendienteAdmin) { formPendienteAdmin.submit(); formPendienteAdmin = null; }
});
btnCancelarEliminarProductoAdmin.addEventListener('click', () => {
  dialogEliminarProductoAdmin.close();
  formPendienteAdmin = null;
});