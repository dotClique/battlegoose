# Battlegoose

The ultimate turn-based online multiplayer game that will litterally give you goosebumps.

## Development

Commands are run from the root directory (battlegoose/).

### Setup

This project only supports to be run in an android emulator or an actual android device.

```bash
./gradlew android:run
```

### Tests

```bash
./gradlew test
```

### Linting

```bash
./gradlew ktlintFormat
```

# Design

- maxHealth: The amount of damage a unit can take before it dies.
- attack: The amount of damage each unit deals when it attacks.
- defense: The percenteage of the attack stat the unit takes when it is attacked by another unit.
- speed: The number of tiles the unit can move if it decides to use its turn on moving to another tile. '
- range: The number of tiles away another unit can be for this unit to be able to attack it.
- flying: If the unit is a flying unit. This means that is able to walk across obstacles in the battlefield.

In the class diagram, the 'health' stat denotes current health in-game.
Units do not move when they attack.

## Units

- Guard Goose (maxHealth: 110, attack: 25, defense: 50, speed: 1, range: 1, flying: false)
- Delinquent Duck (maxHealth: 80, attack: 40, defense: 0, speed: 3, range: 1, flying: false)
- Spitfire Seagull (maxHealth: 70, attack: 20, defense: 5, speed: 3, range: 3, flying: true)
- Private Penguin (maxHealth: 100, attack: 30, defense: 10, speed: 2, range: 1, flying: false)

## Spells

Each spell can affect anything in the game state and can only be used once during a match.

- Bird-52: Deal 40 damange all units in the middle 2 columns of the battlefield.
- Adrenalin Shot: Increase your action points by 1 for the next 3 turns. Thus, the total actions points the next three turns is 2, unless there is another condition that affects the hero's action points.
- Ephemeral Allegience: One random unit the opponent controls joins your army and is now in your control for 3 turns. The position and stats of that unit stays the same.

## Heroes

Each Hero has 1 spell which can only be used once.

- Admiral Albatraoz (spell: Bird-52)
- Sergeant Swan (spell: Patriotic Persuation)
- Major Mallard (spell: Ephemeral Allegience)
