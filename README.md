<img src=https://battlegoo.se/goose.png width=250 />

# BattleGoose

The ultimate turn-based online multiplayer game that will literally give you goosebumps.

Get the game at [battlegoo.se](https://battlegoo.se)!

<a href="https://trailer.battlegoo.se"><img src="https://i.imgur.com/77hZr8C.png" alt="Watch the BattleGoose trailer!" width="720"/></a>

## Development

Commands are run from the root directory (`battlegoose/`).

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
- defense: The percentage decrease of the attack stat the unit takes when it is attacked by another unit.
- speed: The number of tiles the unit can move if it decides to use its turn on moving to another tile.
- range: The number of tiles away another unit can be for this unit to be able to attack it.
- flying: If the unit is a flying unit. This means that the unit is able to move across obstacles in the battlefield.

In the class diagram, the 'health' stat denotes current in-game health.
Units do not move when they attack.

## Units

- Guard Goose (maxHealth: 110, attack: 25, defense: 50, speed: 1, range: 1, flying: false)
- Delinquent Duck (maxHealth: 80, attack: 40, defense: 0, speed: 3, range: 1, flying: false)
- Spitfire Seagull (maxHealth: 70, attack: 20, defense: 5, speed: 3, range: 3, flying: true)
- Private Penguin (maxHealth: 100, attack: 30, defense: 10, speed: 2, range: 1, flying: false)

## Spells

Each spell can affect anything in the game state and has a cooldown which denotes how many turns you have to wait from the spell is cast until the spell can be used again.

- Bird-52 (cooldown: 3): Deal 30 damage to all units in the middle columns of the battlefield.
- Adrenaline Shot (cooldown: 6): Increase your action points by 1 for the following 3 turns. Thus, the total actions points the next 3 turns is 2, unless there is another condition that affects the hero's action points.
- Ephemeral Allegiance (cooldown: 5): One random unit the opponent controls joins your army and is now in your control for 3 turns. The position and stats of that unit stays the same.

## Heroes

Each Hero has 1 spell.

- Admiral Albatross (spell: Bird-52)
- Sergeant Swan (spell: Ephemeral Allegiance)
- Major Mallard (spell: Adrenaline Shot)


# Technical details

## GitLab-runners

This project has 1 project specific gitlab-runner on a VM at Nardo. It is name 'gitlab-runner-frat-no1'. The default image is 'alpine:latest'. If this causes any issues, please notify the team so it can be fixed.

The gitlab-runner config is stored at `/etc/gitlab-runner/config.toml`, where `concurrent = 1` is default, but currently set to `concurrent = 32`.

The runners were registered using the following command:
```bash
sudo gitlab-runner register --non-interactive --url https://gitlab.stud.idi.ntnu.no/ --registration-token $REGISTRATION_TOKEN --executor "docker" --docker-image alpine:latest --run-untagged --description "gitlab-runner-frat-no1337"
```

## Old repository

This project was formerly hosted on GitLab, which means issue and PR numbers restart
after tag v1.0-anonymified. The commits up to and including that point have been
anonymified to hide the identity of one contributor for privacy reasons. To view PRs
and issues from before the move, head to https://gitlab.com/LarsSelbekk/battlegoose,
which is also anonymified but includes GitLab-specific data. Because of the
anonymization process, everything appears to be done by one author, which is not the
case. For comments, the proper author is mentioned in italics underneath, but
assignee/reviewer-information for example is simply lost.
