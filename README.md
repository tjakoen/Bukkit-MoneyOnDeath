# Bukkit-MoneyOnDeath
A simple Bukkit plugin that controls what happens to your money on death. <br />
This is my first bukkit plugin so please bear with me if it looks amateurish. I would very much appreciate tips from you guys if there's a better way to do things, and contributions are always welcome <br/>
I made this plugin to be used on my server. In the server we are aiming for realism, and I noticed that in bukkit there aren't any plugins currently for handling player balances on death. So I went ahead and spent a few hours programming this.

## Dependencies
- Vault
- Vault compatible economy plugin. (We're using the Jobs Reloaded Plugin in the server)

## Features
Currently no permissions, for now only OP's can access commands. Didn't see the need for permissions.
- Remove all player balance on death
- Deduct balance by percentage
- Deduct balance by specific amount
- PVP mode (Deducted money will transfer to killer)
- Toggle OP (Enable or disable for OP's)

## Commands
```
/DeathMoney help
/DeathMoney info - get info on the current configuration
/DeathMoney clear (Default) -
/DeathMoney amount <amount>
/DeathMoney percent <percentage>
/DeathMoney pvp <true|false> (Default: false)
/DeathMoney op <true|false> (Default: false)
```

### Created by Tjakoen A. Stolk (Website)
