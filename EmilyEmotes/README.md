# EmilyEmotes 1.0.0

Selector visual de emotes para **Fabric 1.20.1**, diseñado para usarse junto a **Streamotes**.

## Funciones

- Tecla configurable (por defecto `G`) para abrir la galería.
- Comando cliente `/emotes`.
- Buscador en tiempo real.
- Filtros: Todos, Favoritos, 7TV, Twitch, BTTV y FFZ.
- Clic izquierdo en un emote: abre el chat con su nombre listo para enviar.
- Clic derecho: añade/quita de favoritos.
- Favoritos guardados en `config/emilyemotes.json`.
- Usa mediante reflexión el registro de emotes ya cargado por Streamotes, evitando descargar los mismos emotes otra vez.
- Si Streamotes expone su preview con estilo, la galería reutiliza ese render, incluyendo animaciones cuando Streamotes las mantiene activas.

## Requisitos del cliente

- Minecraft 1.20.1
- Fabric Loader
- Fabric API
- Streamotes

Este mod es **solo de cliente**; no hace falta instalarlo en el servidor.

## Compilar

```bash
gradle build
```

El JAR queda en `build/libs/EmilyEmotes-1.0.0.jar`.
