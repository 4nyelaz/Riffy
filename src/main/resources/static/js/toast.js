"use strict";

class Toast {
    constructor(mensaje, tipo, tiempo) {

        let divToast = document.querySelector("[data-div-toast]");
        if (!divToast) {
            divToast = document.createElement("div");
            divToast.style.position = "fixed";
            divToast.style.top = "4rem";
            divToast.style.right = "1.5rem";
            divToast.style.zIndex = "9999";
            divToast.style.display = "flex";
            divToast.style.flexDirection = "column";
            divToast.style.gap = "0.5rem";
            divToast.dataset.divToast = "yeah";
            document.body.append(divToast);
        }

        const divMensaje = document.createElement("div");
        divMensaje.style.backgroundColor = tipo === Toast.ERROR
            ? "var(--color-accent)"
            : "var(--color-second)";
        divMensaje.style.color = "white";
        divMensaje.style.border = "1px solid rgba(255,255,255,0.15)";
        divMensaje.style.borderRadius = "50px";
        divMensaje.style.padding = "0.65rem 1.25rem";
        divMensaje.style.minWidth = "280px";
        divMensaje.style.maxWidth = "360px";
        divMensaje.style.display = "flex";
        divMensaje.style.alignItems = "center";
        divMensaje.style.gap = "0.75rem";
        divMensaje.style.fontSize = "0.9rem";
        divMensaje.style.fontWeight = "500";
        divMensaje.style.boxShadow = "0 4px 16px rgba(13,27,42,0.18)";

        const icono = document.createElement("i");
        icono.className = tipo === Toast.ERROR ? "fas fa-circle-exclamation" : "fas fa-circle-check";
        icono.style.fontSize = "1.1rem";
        icono.style.flexShrink = "0";

        const texto = document.createElement("span");
        texto.textContent = mensaje;

        divMensaje.append(icono, texto);
        divToast.prepend(divMensaje);

        setTimeout(() => {
            if (document.startViewTransition) {
                document.startViewTransition(() => divMensaje.remove());
            } else {
                divMensaje.remove();
            }
        }, tiempo);
    }

    static INFO = 0;
    static ERROR = 1;
}