UHC

gamerule naturalRegeneration false
gamerule doDaylightCycle false
gamerule doPatrolSpawning false 
gamerule announceAdvancements false
difficulty peaceful
setworldspawn 0 100 0
worldborder center 0 0
worldborder set 101
wb world setcorners -2000 2000 2000 -2000
wb world_nether setcorners -2000 2000 2000 -2000
wb world_the_end setcorners -2000 2000 2000 -2000

LOBBY

Events
- No break
- no place
- no damage entities
- full saturation
Lobby Scoreboard
- online players


SCATTER

Events
- no break
- no place
- no damage entities
- full saturation
- disable join msg and join except if has a perm

clear inventory
difficulty hard
worldborder set 4001
whitelist add all
whitelist on
Scatter Scoreboard
- scattered players
- online players
scatter effects no move

INGAME
Game Scoreboard 
- Timer
- Kills
- Players in survival
Events
- on first join gamemode spectator
- on death fence with head
- on death gamemode spectator
- on death whitelist remove and kick except if has a perm
- on kill check how many players in gamemode survival, if 1 is a win

Other events
- add golden head craft
- hide spectatormode from survivalmode
- command /slots limit
- ommand /nether on/off
- set border time
- set pvp time
- set final heal
- win celebration

/title @a title {"text":"¡Victory!","bold":true,"color":"gold"}
totem packet 

/summon firework_rocket ~ ~1 ~ {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:2,Flicker:0,Trail:1,Colors:[I;3887386,8073150,2651799,4312372],FadeColors:[I;3887386,11250603,4312372,15790320]}]}}}}

/summon firework_rocket ~ ~1 ~ {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:3,Flicker:1,Trail:1,Colors:[I;5320730,2437522,8073150,11250603,6719955],FadeColors:[I;2437522,2651799,11250603,6719955,15790320]}]}}}}

/summon firework_rocket ~ ~1 ~ {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:3,Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;11743532,14602026,12801229,15435844],FadeColors:[I;11743532,14188952,15435844]}]}}}}

on something 
/playsound minecraft:block.respawn_anchor.charge ambient @a 67.46 63.00 -82.62
on start 
/playsound minecraft:block.respawn_anchor.deplete ambient @a 67.46 63.00 -82.62
on pvp
/playsound minecraft:block.respawn_anchor.set_spawn ambient @a 67.46 63.00 -82.62