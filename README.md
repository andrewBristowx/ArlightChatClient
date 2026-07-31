# ArlightChatClient 4.8.0

Mod cliente para NeoForge 1.21.1. Mantiene emotes, scoreboards, colas visuales,
guardarropa, permisos, podio universal y cosméticos; añade las cuatro skins
dimensionales finales de Somita para las escenas de ArlightBingo.

## Somita 4.8.0

- Overworld: maid aventurera.
- Nether: vampiresa elegante.
- End: guía astral ceremonial.
- Celebration: atuendo festivo.
- Animaciones `celebrate_cute` y `celebrate_elegant` sin atravesar la cabeza.
- Partículas personales por dimensión, aparición/desaparición suave y efectos de señalización.
- Preferencia local: `config/arlightchatclient-somita.properties`.

## Compilación

Necesita Java 21. En IntelliJ IDEA abre esta carpeta como proyecto Gradle y ejecuta:

```text
gradlew.bat clean build
```

El resultado esperado es:

```text
build/libs/ArlightChatClient-4.8.0.jar
```

## Instalación

Coloca el JAR únicamente en `mods/` de cada cliente y elimina la versión anterior.
Reinicia Minecraft completamente. Para usar la ranura `TAIL`, el servidor debe ejecutar
ArlightCosmetics 1.4.0 o superior.


## Armadura cosmética 3.6.0

El inventario muestra un panel lateral H/C/L/B similar a Curios. Es una interfaz visual del conjunto `OUTFIT`: no consume casco, pechera, pantalones ni botas vanilla. Cada conjunto se deforma por torso, brazos y piernas usando las poses del PlayerModel.

También incluye mascotas más alejadas, unión reforzada de la cola, corrección de membranas de Somita y emotes por textura en el podio universal.
