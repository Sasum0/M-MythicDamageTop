# MythicDamageTop
Addon para MythicMobs de Paper 26.2 que trackea daño a bosses, arma un leaderboard al terminar el combate y reparte recompensas por posición.
## Comandos
- `/bossspawn <mobId> <x> <y> <z> <world> [amount]` — spawnea un boss configurado en `mobs.yml`.
    - `mobId`: Internal Name de MythicMobs (la key en el YAML, no el `Display`).
    - `amount`: opcional, cantidad a spawnear (default 1).
- `/mdt reload` — recarga `config.yml` y `mobs.yml`.
  Solo cuenta daño directo al boss principal (no a summons). El leaderboard se calcula al morir el boss y se resetea para el próximo spawn.
## Permisos
- `mythicdamagetop.bossspawn` (default: op) — usar `/bossspawn`.
- `mythicdamagetop.admin` (default: op) — usar `/mdt reload`.
## Configuración
- `config.yml`: broadcasts de spawn/muerte (server-wide, sin radio), `line-delay-ticks`, storage (MEMORY).
- `mobs.yml`: bosses trackeados y sus tiers de recompensa por posición exacta (1°, 2°, 3°...).
  Placeholders: `%damage_percent%` se calcula sobre el daño total de jugadores, no sobre el HP máximo del mob. Las líneas de posiciones sin entrada en el top se omiten automáticamente.
  Soporta colores hex (`#RRGGBB`).

### Ejemplo `mobs.yml`

```yaml
mobs:
  BossName:
    enabled: true
    top:
      size: 5
      rewards:
        1:
          commands:
            - "eco give %player_name% 5000"
          message: "&6¡Ganaste el 1er puesto contra %mob_name%!"
          title: "&6TOP #1"
          subtitle: "&e%damage% &7(%damage_percent%%)"
        2:
          commands:
            - "eco give %player_name% 2500"
          message: "&e¡2do puesto!"
          title: "&eTOP #2"
          subtitle: "&e%damage% &7(%damage_percent%%)"
    announce:
      # %top_N_name%, %top_N_damage%, %top_N_damage_percent% según top.size
      lines:
        - ''
        - ' &aHa caido el %mob_name%'
        - ' &a#1 - &f%top_1_name% &7- &eDaño: %top_1_damage% &7(%top_1_damage_percent%%)'
        - ' &a#2 - &f%top_2_name% &7- &eDaño: %top_2_damage% &7(%top_2_damage_percent%%)'
        - ''
    spawn-message:
      lines:
        - ''
        - ' &aℹ &fHa aparecido el %mob_name%'
        - ' &a⏩ &fLucha contra él en &a/warp boss'
        - ''
    spawn-command:
      default-amount: 1
      max-amount: 5
      default-facing: SOUTH
```
