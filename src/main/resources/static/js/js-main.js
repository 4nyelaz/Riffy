"use strict";

/* ------------------------------------------------------------------ */
/* Boton desplegables INDEX
/* ------------------------------------------------------------------ */

function abrirAcordeonDesdeHash() {
    const hash =  window.location.hash;  // obtiene el hash de la URL
    if (hash) {
        const accordionItem =  document.querySelector(hash);
        if (accordionItem) {  // Si existe
            const button =  accordionItem.querySelector('.accordion-button');  // Botón del acordeón
            const collapseElement =  accordionItem.querySelector('.accordion-collapse');
            if (button && collapseElement) {  // Si ambos existen
                // cierra todos los paneles abiertos
                document.querySelectorAll('.accordion-collapse').forEach(el = > {
                    const bsCollapse =  bootstrap.Collapse.getInstance(el);
                    if (bsCollapse) bsCollapse.hide();
                });
                // abre el panel correspondiente al hash
                const bsCollapse =  new bootstrap.Collapse(collapseElement, {
                    toggle: false
                });
                bsCollapse.show();
                setTimeout(() = > {
                    accordionItem.scrollIntoView({
                        behavior: 'smooth',
                        block: 'center'
                    });
                    history.replaceState(null, null, window.location.pathname);  // elimina el hash
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
/* ir a login o resgistro desde index
/* ------------------------------------------------------------------ */

document.addEventListener('DOMContentLoaded', function () {

    const pestanaLogin =  document.getElementById('pestana-login');
    const pestanaRegistro =  document.getElementById('pestana-registro');

    if (!pestanaLogin || !pestanaRegistro) return;

    const secLogin =  document.getElementById('sec-login');
    const secRegistro =  document.getElementById('sec-registro');
    const enlaceRegistro =  document.getElementById('enlace-registro');
    const enlaceLogin =  document.getElementById('enlace-login');

    function cambiarSeccion(seccion) {
        const esLogin =  seccion = == 'login';

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
    const params =  new URLSearchParams(window.location.search);
    if (params.get('section') = == 'register') cambiarSeccion('registro');
    else cambiarSeccion('login');

});

/* ------------------------------------------------------------------ */
/* validaciones
/* ------------------------------------------------------------------ */
document.addEventListener("DOMContentLoaded", () = > {

    const nombre =  document.getElementById("nombre");
    const usuario =  document.getElementById("usuario");
    const contrasena =  document.getElementById("contrasena");

    // ── helpers ────────────────────────────────────────────────────────
    function ok(el, texto) {
        el.innerHTML =  `<i class="fa-solid fa-circle-check"></i> ${texto}`;
        el.style.color =  "#4CAF50";
    }

    function err(el, texto) {
        el.innerHTML =  `<i class="fa-solid fa-circle-xmark"></i> ${texto}`;
        el.style.color =  "#ff4d4d";
    }

    function limpiar(el) {
        el.innerHTML =  "";
    }

    // ── NOMBRE ─────────────────────────────────────────────────────────
    const msgNombre =  document.getElementById("validacion-nombre");

    nombre.addEventListener("focus", () = > limpiar(msgNombre));

    nombre.addEventListener("input", () = > {
        if (nombre.value.length >= 3) {
            ok(msgNombre, "Nombre válido");
        } else {
            err(msgNombre, "Mínimo 3 caracteres");
        }
    });

    // ── USUARIO ────────────────────────────────────────────────────────
    const msgUsuario =  document.getElementById("validacion-usuario");
    const regex =  /^[a-zA-Z0-9_]+$/;
    let debounceTimer;

    usuario.addEventListener("focus", () = > limpiar(msgUsuario));

    usuario.addEventListener("input", async () = > {
        const valor =  usuario.value;

        // 1. Validación local primero
        if (valor.length < 4 || !regex.test(valor)) {
            err(msgUsuario, "Solo letras, números y _ (mín. 4)");
            clearTimeout(debounceTimer);
            return;
        }

        // 2. Pasa la local → comprobar en servidor con debounce
        msgUsuario.innerHTML =  "Comprobando…";
        msgUsuario.style.color =  "var(--color-text-secondary, #888)";

        clearTimeout(debounceTimer);
        debounceTimer =  setTimeout(async () = > {
            try {
                const res =  await fetch(`/api/usuario-existe?usuario=${encodeURIComponent(valor)}`);
                const existe =  await res.json();

                if (existe) {
                    err(msgUsuario, "Usuario ya en uso");
                } else {
                    ok(msgUsuario, "Usuario disponible");
                }
            } catch {
                err(msgUsuario, "Error al comprobar disponibilidad");
            }
        }, 400);
    });

    // ── CONTRASEÑA ────────────────────────────────────────────────────
    const msgContrasena =  document.getElementById("validacion-contrasena");

    contrasena.addEventListener("focus", () = > limpiar(msgContrasena));

    contrasena.addEventListener("input", () = > {
        const tieneMayus =  /[A-Z]/.test(contrasena.value);
        const tieneNumero =  /[0-9]/.test(contrasena.value);

        if (contrasena.value.length >= 8 && tieneMayus && tieneNumero) {
            ok(msgContrasena, "Contraseña segura");
        } else {
            err(msgContrasena, "8 caracteres, 1 mayúscula y 1 número");
        }
    });

    // ── EMAIL ──────────────────────────────────────────────────────────
    const email =  document.getElementById("email");
    const msgEmail =  document.getElementById("validacion-email");

    if (email) {
        email.addEventListener("focus", () = > limpiar(msgEmail));

        email.addEventListener("input", () = > {
            const val =  email.value.trim();
            if (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
                ok(msgEmail, "Email válido");
            } else {
                err(msgEmail, "Introduce un email válido");
            }
        });
    }

    // ── CONFIRMAR CONTRASEÑA ──────────────────────────────────────────
    const contrasena2 =  document.getElementById("contrasena2");
    const msgContrasena2 =  document.getElementById("validacion-contrasena2");

    if (contrasena2) {
        contrasena2.addEventListener("focus", () = > limpiar(msgContrasena2));

        contrasena2.addEventListener("input", () = > {
            if (contrasena2.value = == contrasena.value) {
                ok(msgContrasena2, "Las contraseñas coinciden");
            } else {
                err(msgContrasena2, "Las contraseñas no coinciden");
            }
        });
    }

    // ── SUBMIT ───────────────────────────────────────────────────────
    const form =  document.querySelector("#sec-registro form");

    form.addEventListener("submit", (e) = > {
        let valido =  true;

        if (nombre.value.length < 3) {
            err(msgNombre, "Mínimo 3 caracteres");
            valido =  false;
        }

        if (usuario.value.length < 4 || !regex.test(usuario.value)) {
            err(msgUsuario, "Solo letras, números y _ (mín. 4)");
            valido =  false;
        }

        // email
        if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
            err(msgEmail, "Introduce un email válido");
            valido =  false;
        }

        const tieneMayus =  /[A-Z]/.test(contrasena.value);
        const tieneNumero =  /[0-9]/.test(contrasena.value);
        if (contrasena.value.length < 8 || !tieneMayus || !tieneNumero) {
            err(msgContrasena, "8 caracteres, 1 mayúscula y 1 número");
            valido =  false;
        }

        // confirmar contraseña
        if (contrasena2 && contrasena2.value !== contrasena.value) {
            err(msgContrasena2, "Las contraseñas no coinciden");
            valido =  false;
        }

        if (!valido) {
            e.preventDefault();
            new Toast("Revisa los campos del formulario", Toast.ERROR, 4000);
        }
    });
});



/* ------------------------------------------------------------------ */
/* dialog — eliminar producto
/* ------------------------------------------------------------------ */

const dialogEliminar =  document.createElement('dialog');
dialogEliminar.id =  'dialogEliminar';

const boxEliminar =  document.createElement('div');
boxEliminar.className =  'dialog-box';

const h5Eliminar =  document.createElement('h5');
h5Eliminar.textContent =  '¿Eliminar producto?';

const pEliminar =  document.createElement('p');
pEliminar.id =  'dialogEliminarNombre';

const actionsEliminar =  document.createElement('div');
actionsEliminar.className =  'dialog-actions';

const btnConfirmarEliminar =  document.createElement('button');
btnConfirmarEliminar.id =  'btnConfirmarEliminar';
btnConfirmarEliminar.className =  'btn btn-danger btn-sm rounded-pill';
btnConfirmarEliminar.textContent =  'Confirmar';

const btnCancelarEliminar =  document.createElement('button');
btnCancelarEliminar.id =  'btnCancelarEliminar';
btnCancelarEliminar.className =  'btn btn-secondary btn-sm rounded-pill';
btnCancelarEliminar.textContent =  'Cancelar';

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

const dialogEditar =  document.createElement('dialog');
dialogEditar.id =  'dialogEditar';

const boxEditar =  document.createElement('div');
boxEditar.className =  'dialog-box';

const h5Editar =  document.createElement('h5');
h5Editar.textContent =  'Editar producto';

const inputHiddenId =  document.createElement('input');
inputHiddenId.type =  'hidden';
inputHiddenId.id =  'editarId';

// crea un wrapper label + campo
function crearCampo(labelText, elemento) {
    const div =  document.createElement('div');
    div.className =  'mb-2';

    const label =  document.createElement('label');
    label.textContent =  labelText;

    div.appendChild(label);
    div.appendChild(elemento);
    return div;
}

// input genérico
function crearInput(id, tipo =  'text') {
    const input =  document.createElement('input');
    input.type =  tipo;
    input.id =  id;
    input.className =  'form-control form-control-sm';
    return input;
}

// select con sus opciones
function crearSelect(id, opciones) {
    const select =  document.createElement('select');
    select.id =  id;
    select.className =  'form-select form-select-sm';
    opciones.forEach(op = > {
        const option =  document.createElement('option');
        option.value =  op;
        option.textContent =  op;
        select.appendChild(option);
    });
    return select;
}

const inputImagenesVisible =  document.createElement('input');
inputImagenesVisible.type =  'file';
inputImagenesVisible.id =  'editarImagenes';
inputImagenesVisible.className =  'form-control form-control-sm';
inputImagenesVisible.multiple =  true;
inputImagenesVisible.accept =  'image/*';

const contenedorPreview =  document.createElement('div');
contenedorPreview.id =  'previewImagenes';
contenedorPreview.style.cssText =  'display:flex; flex-wrap:wrap; gap:6px; margin-top:6px;';

inputImagenesVisible.addEventListener('change', () = > {
    contenedorPreview.innerHTML =  '';

    // Convertir FileList a array para poder filtrar
    let archivosSeleccionados =  Array.from(inputImagenesVisible.files);

    function renderPreview() {
        contenedorPreview.innerHTML =  '';
        archivosSeleccionados.forEach((file, index) = > {
            const tag =  document.createElement('div');
            tag.style.cssText =  'display:flex; align-items:center; gap:4px; background:var(--color-bg-alt); border-radius:20px; padding:4px 10px; font-size:12px;';

            const nombre =  document.createElement('span');
            nombre.textContent =  file.name;

            const cruz =  document.createElement('button');
            cruz.type =  'button';
            cruz.innerHTML =  '&times;';
            cruz.style.cssText =  'background:none; border:none; color:var(--color-accent); font-size:14px; cursor:pointer; padding:0; line-height:1;';

            cruz.addEventListener('click', () = > {
                archivosSeleccionados.splice(index, 1);

                // Reconstruir el FileList con los archivos restantes
                const dt =  new DataTransfer();
                archivosSeleccionados.forEach(f = > dt.items.add(f));
                inputImagenesVisible.files =  dt.files;

                renderPreview();
            });

            tag.appendChild(nombre);
            tag.appendChild(cruz);
            contenedorPreview.appendChild(tag);
        });
    }

    renderPreview();
});

const inputTitulo =  crearInput('editarTitulo');
const inputArtista =  crearInput('editarArtista');
const inputPrecio =  crearInput('editarPrecio', 'number');

const textareaDescripcion =  document.createElement('textarea');
textareaDescripcion.id =  'editarDescripcion';
textareaDescripcion.className =  'form-control form-control-sm';

const selectEstado =  crearSelect('editarEstado', ['Disponible', 'Vendido', 'Reservado']);
const selectCategoria =  crearSelect('editarCategoria', ['Vinilo', 'CD']);
const selectFormato =  crearSelect('editarFormato', ['Nuevo', 'Muy Bueno', 'Bueno', 'Usado']);

const actionsEditar =  document.createElement('div');
actionsEditar.className =  'dialog-actions';

const btnConfirmarEditar =  document.createElement('button');
btnConfirmarEditar.id =  'btnConfirmarEditar';
btnConfirmarEditar.className =  'btn btn-primary btn-sm rounded-pill';
btnConfirmarEditar.textContent =  'Guardar';

const btnCancelarEditar =  document.createElement('button');
btnCancelarEditar.id =  'btnCancelarEditar';
btnCancelarEditar.className =  'btn btn-secondary btn-sm rounded-pill';
btnCancelarEditar.textContent =  'Cancelar';

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
const campoimagenes =  crearCampo('Imágenes (opcional)', inputImagenesVisible);
campoimagenes.appendChild(contenedorPreview);
boxEditar.appendChild(campoimagenes);
boxEditar.appendChild(actionsEditar);
dialogEditar.appendChild(boxEditar);

boxEditar.appendChild(actionsEditar);
dialogEditar.appendChild(boxEditar);
document.body.appendChild(dialogEditar);


/* ------------------------------------------------------------------ */
/* lógica — eliminar
/* ------------------------------------------------------------------ */

document.addEventListener('click', (e) = > {
    const btn =  e.target.closest('.btn-eliminar');
    if (!btn) return;

    e.preventDefault();
    pEliminar.textContent =  `"${btn.dataset.titulo}"`;
    dialogEliminar.showModal();

    btnConfirmarEliminar.onclick =  () = > {
        window.location.href =  `/eliminarproducto/${btn.dataset.id}`;
    };

    btnCancelarEliminar.onclick =  () = > {
        dialogEliminar.close();
    };
});


/* ------------------------------------------------------------------ */
/* lógica — editar
/* ------------------------------------------------------------------ */

document.addEventListener('click', (e) = > {
    const btn =  e.target.closest('.btn-editar');
    if (!btn) return;

    e.preventDefault();
    const d =  btn.dataset;

    // rellenamos el formulario con los datos del producto
    inputHiddenId.value =  d.id;
    inputTitulo.value =  d.titulo;
    inputArtista.value =  d.artista;
    textareaDescripcion.value =  d.descripcion;
    inputPrecio.value =  d.precio;
    selectEstado.value =  d.estado;
    selectCategoria.value =  d.categoria;
    selectFormato.value =  d.formato;

    dialogEditar.showModal();

    btnCancelarEditar.onclick =  () = > dialogEditar.close();

    btnConfirmarEditar.onclick =  () = > {
        const form =  document.createElement('form');
        form.method =  'POST';
        form.action =  `/editarproducto/${inputHiddenId.value}`;
        form.enctype =  'multipart/form-data';

        const campos =  {
            titulo: inputTitulo.value,
            artista: inputArtista.value,
            descripcion: textareaDescripcion.value,
            precio: inputPrecio.value,
            estado: selectEstado.value,
            categoria: selectCategoria.value,
            formato: selectFormato.value,
        };

        for (const [name, value] of Object.entries(campos)) {
            const input =  document.createElement('input');
            input.type =  'hidden';
            input.name =  name;
            input.value =  value;
            form.appendChild(input);
        }

        // Mover el input file real al form (no se puede clonar con ficheros)
        const fileInput =  document.querySelector('#editarImagenes');
        fileInput.name =  'imagenes';
        form.appendChild(fileInput);

        document.body.appendChild(form);
        form.submit();
    };
});

/* ------------------------------------------------------------------ */
/* imagenes - crear producto */
/* ------------------------------------------------------------------ */

const inputImagenesCrear =  document.querySelector('#imagenes');

if (inputImagenesCrear) {

    const contenedorPreviewCrear =  document.createElement('div');
    contenedorPreviewCrear.style.cssText =  'display:flex; flex-wrap:wrap; gap:6px; margin-top:6px;';
    inputImagenesCrear.parentNode.insertAdjacentElement('afterend', contenedorPreviewCrear);

    let archivosCrear =  [];

    inputImagenesCrear.addEventListener('change', () = > {
        archivosCrear =  Array.from(inputImagenesCrear.files);
        renderPreviewCrear();
    });

    function renderPreviewCrear() {
        contenedorPreviewCrear.innerHTML =  '';
        archivosCrear.forEach((file, index) = > {
            const tag =  document.createElement('div');
            tag.style.cssText =  'display:flex; align-items:center; gap:4px; background:var(--color-bg-alt); border-radius:20px; padding:4px 10px; font-size:12px;';

            const nombre =  document.createElement('span');
            nombre.textContent =  file.name;

            const cruz =  document.createElement('button');
            cruz.type =  'button';
            cruz.innerHTML =  '&times;';
            cruz.style.cssText =  'background:none; border:none; color:var(--color-accent); font-size:14px; cursor:pointer; padding:0; line-height:1;';

            cruz.addEventListener('click', () = > {
                archivosCrear.splice(index, 1);
                const dt =  new DataTransfer();
                archivosCrear.forEach(f = > dt.items.add(f));
                inputImagenesCrear.files =  dt.files;
                renderPreviewCrear();
            });

            tag.appendChild(nombre);
            tag.appendChild(cruz);
            contenedorPreviewCrear.appendChild(tag);
        });
    }
}

/* ------------------------------------------------------------------ */
/* imagenes - crear producto */
/* ------------------------------------------------------------------ */
document.addEventListener('DOMContentLoaded', function () {
    const selectOrdenar =  document.getElementById('ordenarProductos');
    const contenedor =  document.querySelector('.productos-carrusel');

    if (!selectOrdenar || !contenedor) return;

    // Guardamos el orden original de las tarjetas
    const ordenOriginal =  Array.from(contenedor.children);

    selectOrdenar.addEventListener('change', function () {
        const criterio =  this.value;

        // Si es "defecto", restauramos el orden original
        if (criterio = == 'defecto') {
            ordenOriginal.forEach(tarjeta = > contenedor.appendChild(tarjeta));
            contenedor.scrollLeft =  0;
            return;
        }

        // Obtener todas las tarjetas como array
        const tarjetas =  Array.from(contenedor.children);

        // Ejecutar la función según el criterio seleccionado
        switch (criterio) {
            case 'az':
                ordenarAZ(tarjetas, contenedor);
                break;
            case 'za':
                ordenarZA(tarjetas, contenedor);
                break;
            case 'precio-asc':
                ordenarPrecioAsc(tarjetas, contenedor);
                break;
            case 'precio-desc':
                ordenarPrecioDesc(tarjetas, contenedor);
                break;
            case 'fecha-desc':
                ordenarFechaDesc(tarjetas, contenedor);
                break;
            case 'fecha-asc':
                ordenarFechaAsc(tarjetas, contenedor);
                break;
        }
    });
});

// ─── Funciones de ordenación ────────────────────────────────────────

function obtenerDatos(tarjeta) {
    const btnEditar =  tarjeta.querySelector('.btn-editar');
    if (!btnEditar) return { titulo: '', precio: 0, fecha: null };

    return {
        titulo: (btnEditar.dataset.titulo || '').toLowerCase(),
        precio: parseFloat(btnEditar.dataset.precio) || 0,
        fecha: btnEditar.dataset.fecha ? new Date(btnEditar.dataset.fecha) : null
    };
}

function reinsertarTarjetas(tarjetas, contenedor) {
    tarjetas.forEach(tarjeta = > contenedor.appendChild(tarjeta));
    contenedor.scrollLeft =  0;
}

// A – Z
function ordenarAZ(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const tituloA =  obtenerDatos(a).titulo;
        const tituloB =  obtenerDatos(b).titulo;
        return tituloA.localeCompare(tituloB);
    });
    reinsertarTarjetas(tarjetas, contenedor);

}

// Z – A
function ordenarZA(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const tituloA =  obtenerDatos(a).titulo;
        const tituloB =  obtenerDatos(b).titulo;
        return tituloB.localeCompare(tituloA);
    });
    reinsertarTarjetas(tarjetas, contenedor);
}

// Precio: menor a mayor
function ordenarPrecioAsc(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const precioA =  obtenerDatos(a).precio;
        const precioB =  obtenerDatos(b).precio;
        return precioA - precioB;
    });
    reinsertarTarjetas(tarjetas, contenedor);
}

// Precio: mayor a menor
function ordenarPrecioDesc(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const precioA =  obtenerDatos(a).precio;
        const precioB =  obtenerDatos(b).precio;
        return precioB - precioA;
    });
    reinsertarTarjetas(tarjetas, contenedor);
}

// Más reciente primero
function ordenarFechaDesc(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const fechaA =  obtenerDatos(a).fecha;
        const fechaB =  obtenerDatos(b).fecha;

        if (!fechaA && !fechaB) return 0;
        if (!fechaA) return 1;
        if (!fechaB) return -1;
        return fechaB - fechaA;
    });
    reinsertarTarjetas(tarjetas, contenedor);
}

// Más antiguo primero
function ordenarFechaAsc(tarjetas, contenedor) {
    tarjetas.sort((a, b) = > {
        const fechaA =  obtenerDatos(a).fecha;
        const fechaB =  obtenerDatos(b).fecha;

        if (!fechaA && !fechaB) return 0;
        if (!fechaA) return 1;
        if (!fechaB) return -1;
        return fechaA - fechaB;
    });
    reinsertarTarjetas(tarjetas, contenedor);
}

const bellBtn =  document.getElementById('bellBtn');
const notifPanel =  document.getElementById('notifPanel');
if (bellBtn) {
    bellBtn.addEventListener('click', e = > {
        e.stopPropagation();
        notifPanel.style.display =  notifPanel.style.display = == 'none' ? 'block' : 'none';
    });
    document.addEventListener('click', e = > {
        if (!document.getElementById('bellContainer').contains(e.target))
            notifPanel.style.display =  'none';
    });
}


function previewFoto(input) {
    if (input.files && input.files[0]) {
        const reader =  new FileReader();
        reader.onload =  e = > {
            const preview =  document.getElementById('avatarPreview');
            if (preview.tagName = == 'DIV') {
                // era la inicial, la reemplazamos por img
                const img =  document.createElement('img');
                img.id =  'avatarPreview';
                img.className =  'rounded-circle object-fit-cover';
                img.style.cssText =  'width:80px; height:80px; border: 3px solid var(--color-accent);';
                img.alt =  'Foto de perfil';
                preview.replaceWith(img);
            }
            document.getElementById('avatarPreview').src =  e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// TOAST

document.addEventListener("DOMContentLoaded", () = > {

    const show =  (selector, type) = > {
        const el =  document.querySelector(selector);
        if (el && el.dataset.mensaje) {
            new Toast(el.dataset.mensaje, type, 4000);
        }
    };

    show("[data-toast-logout]", Toast.INFO);
    show("[data-toast-login]", Toast.ERROR);
    show("[data-toast-error]", Toast.ERROR);
    show("[data-toast-success]", Toast.INFO);
    show("[data-toast-perfil]", Toast.INFO);
    show("[data-toast-productoExito]", Toast.INFO);
    show("[data-toast-productoError]", Toast.ERROR);
    show("[data-toast-adminExito]", Toast.INFO);
    show("[data-toast-sesionCaducada]", Toast.ERROR);
});

window.onload =  function () {
    var contenedor =  document.getElementById('contenedorMensajes');
    contenedor.scrollTop =  contenedor.scrollHeight;
};