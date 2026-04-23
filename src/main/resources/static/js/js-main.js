"use strict";

/* ------------------------------------------------------------------ */
/* acordeón por hash
/* ------------------------------------------------------------------ */

function abrirAcordeonDesdeHash() {
    const hash = window.location.hash;

    if (hash) {
        const accordionItem = document.querySelector(hash);

        if (accordionItem) {
            const button = accordionItem.querySelector('.accordion-button');
            const collapseElement = accordionItem.querySelector('.accordion-collapse');

            if (button && collapseElement) {
                // cerramos todos primero
                document.querySelectorAll('.accordion-collapse').forEach(el => {
                    const bsCollapse = bootstrap.Collapse.getInstance(el);
                    if (bsCollapse) bsCollapse.hide();
                });

                // abrimos el que toca
                const bsCollapse = new bootstrap.Collapse(collapseElement, {
                    toggle: false
                });
                bsCollapse.show();

                // scroll suave al elemento
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

// al cargar la página
document.addEventListener('DOMContentLoaded', abrirAcordeonDesdeHash);

// y también si cambia el hash sin recargar
window.addEventListener('hashchange', abrirAcordeonDesdeHash);


/* ------------------------------------------------------------------ */
/* login / registro
/* ------------------------------------------------------------------ */

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

    // si viene con ?section=register abrimos directamente el registro
    const params = new URLSearchParams(window.location.search);
    if (params.get('section') === 'register') cambiarSeccion('registro');
    else cambiarSeccion('login');

});


/* ------------------------------------------------------------------ */
/* dialog — eliminar producto
/* ------------------------------------------------------------------ */

const dialogEliminar = document.createElement('dialog');
dialogEliminar.id = 'dialogEliminar';

const boxEliminar = document.createElement('div');
boxEliminar.className = 'dialog-box';

const h5Eliminar = document.createElement('h5');
h5Eliminar.textContent = '¿Eliminar producto?';

const pEliminar = document.createElement('p');
pEliminar.id = 'dialogEliminarNombre';

const actionsEliminar = document.createElement('div');
actionsEliminar.className = 'dialog-actions';

const btnConfirmarEliminar = document.createElement('button');
btnConfirmarEliminar.id = 'btnConfirmarEliminar';
btnConfirmarEliminar.className = 'btn btn-danger btn-sm rounded-pill';
btnConfirmarEliminar.textContent = 'Confirmar';

const btnCancelarEliminar = document.createElement('button');
btnCancelarEliminar.id = 'btnCancelarEliminar';
btnCancelarEliminar.className = 'btn btn-secondary btn-sm rounded-pill';
btnCancelarEliminar.textContent = 'Cancelar';

actionsEliminar.appendChild(btnConfirmarEliminar);
actionsEliminar.appendChild(btnCancelarEliminar);

boxEliminar.appendChild(h5Eliminar);
boxEliminar.appendChild(pEliminar);
boxEliminar.appendChild(actionsEliminar);

dialogEliminar.appendChild(boxEliminar);
document.body.appendChild(dialogEliminar);


/* ------------------------------------------------------------------ */
/* dialog — editar producto
/* ------------------------------------------------------------------ */

const dialogEditar = document.createElement('dialog');
dialogEditar.id = 'dialogEditar';

const boxEditar = document.createElement('div');
boxEditar.className = 'dialog-box';

const h5Editar = document.createElement('h5');
h5Editar.textContent = 'Editar producto';

const inputHiddenId = document.createElement('input');
inputHiddenId.type = 'hidden';
inputHiddenId.id = 'editarId';

// crea un wrapper label + campo
function crearCampo(labelText, elemento) {
    const div = document.createElement('div');
    div.className = 'mb-2';

    const label = document.createElement('label');
    label.textContent = labelText;

    div.appendChild(label);
    div.appendChild(elemento);
    return div;
}

// input genérico
function crearInput(id, tipo = 'text') {
    const input = document.createElement('input');
    input.type = tipo;
    input.id = id;
    input.className = 'form-control form-control-sm';
    return input;
}

// select con sus opciones
function crearSelect(id, opciones) {
    const select = document.createElement('select');
    select.id = id;
    select.className = 'form-select form-select-sm';
    opciones.forEach(op => {
        const option = document.createElement('option');
        option.value = op;
        option.textContent = op;
        select.appendChild(option);
    });
    return select;
}

const inputTitulo = crearInput('editarTitulo');
const inputArtista = crearInput('editarArtista');
const inputPrecio = crearInput('editarPrecio', 'number');

const textareaDescripcion = document.createElement('textarea');
textareaDescripcion.id = 'editarDescripcion';
textareaDescripcion.className = 'form-control form-control-sm';

const selectEstado = crearSelect('editarEstado', ['Disponible', 'Vendido', 'Reservado']);
const selectCategoria = crearSelect('editarCategoria', ['Vinilo', 'CD']);
const selectFormato = crearSelect('editarFormato', ['Nuevo', 'Muy Bueno', 'Bueno', 'Usado']);

const actionsEditar = document.createElement('div');
actionsEditar.className = 'dialog-actions';

const btnConfirmarEditar = document.createElement('button');
btnConfirmarEditar.id = 'btnConfirmarEditar';
btnConfirmarEditar.className = 'btn btn-primary btn-sm rounded-pill';
btnConfirmarEditar.textContent = 'Guardar';

const btnCancelarEditar = document.createElement('button');
btnCancelarEditar.id = 'btnCancelarEditar';
btnCancelarEditar.className = 'btn btn-secondary btn-sm rounded-pill';
btnCancelarEditar.textContent = 'Cancelar';

actionsEditar.appendChild(btnConfirmarEditar);
actionsEditar.appendChild(btnCancelarEditar);

boxEditar.appendChild(h5Editar);
boxEditar.appendChild(inputHiddenId);
boxEditar.appendChild(crearCampo('Título', inputTitulo));
boxEditar.appendChild(crearCampo('Artista', inputArtista));
boxEditar.appendChild(crearCampo('Descripción', textareaDescripcion));
boxEditar.appendChild(crearCampo('Precio', inputPrecio));
boxEditar.appendChild(crearCampo('Estado', selectEstado));
boxEditar.appendChild(crearCampo('Categoría', selectCategoria));
boxEditar.appendChild(crearCampo('Formato', selectFormato));
boxEditar.appendChild(actionsEditar);

dialogEditar.appendChild(boxEditar);
document.body.appendChild(dialogEditar);


/* ------------------------------------------------------------------ */
/* lógica — eliminar
/* ------------------------------------------------------------------ */

document.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-eliminar');
    if (!btn) return;

    e.preventDefault();
    pEliminar.textContent = `"${btn.dataset.titulo}"`;
    dialogEliminar.showModal();

    btnConfirmarEliminar.onclick = () => {
        window.location.href = `/eliminarproducto/${btn.dataset.id}`;
    };

    btnCancelarEliminar.onclick = () => {
        dialogEliminar.close();
    };
});


/* ------------------------------------------------------------------ */
/* lógica — editar
/* ------------------------------------------------------------------ */

document.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-editar');
    if (!btn) return;

    e.preventDefault();
    const d = btn.dataset;

    // rellenamos el formulario con los datos del producto
    inputHiddenId.value = d.id;
    inputTitulo.value = d.titulo;
    inputArtista.value = d.artista;
    textareaDescripcion.value = d.descripcion;
    inputPrecio.value = d.precio;
    selectEstado.value = d.estado;
    selectCategoria.value = d.categoria;
    selectFormato.value = d.formato;

    dialogEditar.showModal();

    btnCancelarEditar.onclick = () => dialogEditar.close();

    btnConfirmarEditar.onclick = () => {
        // creamos un form dinámico y lo enviamos
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/editarproducto/${d.id}`;

        const campos = {
            titulo: inputTitulo.value,
            artista: inputArtista.value,
            descripcion: textareaDescripcion.value,
            precio: inputPrecio.value,
            estado: selectEstado.value,
            categoria: selectCategoria.value,
            formato: selectFormato.value,
        };

        for (const [name, value] of Object.entries(campos)) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = name;
            input.value = value;
            form.appendChild(input);
        }

        document.body.appendChild(form);
        form.submit();
    };
});