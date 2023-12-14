/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2023 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.commands.stats.impl

import gg.essential.universal.utils.MCHoverEventAction
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
import gg.skytils.hypixel.types.skyblock.Member
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.Skytils.Companion.failPrefix
import gg.skytils.skytilsmod.commands.stats.StatCommand
import gg.skytils.skytilsmod.utils.NumberUtil.nf
import gg.skytils.skytilsmod.utils.SkillUtils
import gg.skytils.skytilsmod.utils.append
import gg.skytils.skytilsmod.utils.formattedName
import gg.skytils.skytilsmod.utils.setHoverText
import skytils.hylin.request.HypixelAPIException
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


object CataCommand : StatCommand("skytilscata") {

    override fun displayStats(username: String, uuid: UUID, profileData: Member) {
        val playerResponse = try {
            Skytils.hylinAPI.getPlayerSync(uuid)
        } catch (e: HypixelAPIException) {
            printMessage(
                "$failPrefix §cFailed to get dungeon stats: ${
                    e.message
                }"
            )
            return
        }

        try {
            val dungeonsData = profileData.dungeons ?: run {
                printMessage("$failPrefix §c${username} has not entered any dungeons!")
                return
            }

            val catacombsObj = dungeonsData.dungeon_types["catacombs"]
            if (catacombsObj?.experience == null) {
                printMessage("$failPrefix §c${username} has not entered The Catacombs!")
                return
            }
            val cataData = catacombsObj.normal
            val masterCataData = catacombsObj.master

            val cataLevel =
                SkillUtils.calcXpWithProgress(catacombsObj.experience ?: 0.0, SkillUtils.dungeoneeringXp.values)

            val archXP = dungeonsData.player_classes["archer"]?.experience ?: 0.0
            val bersXP = dungeonsData.player_classes["berserk"]?.experience ?: 0.0
            val healerXP = dungeonsData.player_classes["healer"]?.experience ?: 0.0
            val mageXP = dungeonsData.player_classes["mage"]?.experience ?: 0.0
            val tankXP = dungeonsData.player_classes["tank"]?.experience ?: 0.0

            val archLevel =
                SkillUtils.calcXpWithProgress(
                    archXP,
                    SkillUtils.dungeoneeringXp.values
                )
            val bersLevel =
                SkillUtils.calcXpWithProgress(
                    bersXP,
                    SkillUtils.dungeoneeringXp.values
                )
            val healerLevel =
                SkillUtils.calcXpWithProgress(
                    healerXP,
                    SkillUtils.dungeoneeringXp.values
                )
            val mageLevel =
                SkillUtils.calcXpWithProgress(
                    mageXP,
                    SkillUtils.dungeoneeringXp.values
                )
            val tankLevel =
                SkillUtils.calcXpWithProgress(
                    tankXP,
                    SkillUtils.dungeoneeringXp.values
                )

            val secrets = playerResponse.achievements.getOrDefault("skyblock_treasure_hunter", 0)

            val classAvgOverflow = (archLevel + bersLevel + healerLevel + mageLevel + tankLevel) / 5.0
            val classAvgCapped =
                (archLevel.coerceAtMost(50.0) + bersLevel.coerceAtMost(50.0) + healerLevel.coerceAtMost(50.0) + mageLevel.coerceAtMost(
                    50.0
                ) + tankLevel.coerceAtMost(50.0)) / 5.0

            val component = UMessage("§a➜ Catacombs Statistics Viewer\n")
                .append(
                    "§2§l ❣ §7§oYou are looking at data for ${playerResponse.formattedName}§7§o.\n\n"
                )
                .append("§a§l➜ Catacombs Levels:\n")
                .append(
                    UTextComponent("§d ☠ Cata Level: §l➡ §e${nf.format(cataLevel)}\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(catacombsObj.experience ?: 0.0)} XP"
                    )
                )
                .append("§9 ☠ Class Avg: §l➡ §e${nf.format(classAvgCapped)} §7(${nf.format(classAvgOverflow)})\n\n")
                .append(
                    UTextComponent("§6 ☣ Archer Level: §l➡ §e${nf.format(archLevel)}\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(archXP)} XP"
                    )
                )
                .append(
                    UTextComponent("§c ⚔ Berserk Level: §l➡ §e${nf.format(bersLevel)}\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(bersXP)} XP"
                    )
                )
                .append(
                    UTextComponent("§a ❤ Healer Level: §l➡ §e${nf.format(healerLevel)}\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(healerXP)} XP"
                    )
                )
                .append(
                    UTextComponent("§b ✎ Mage Level: §l➡ §e${nf.format(mageLevel)}\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(mageXP)} XP"
                    )
                )
                .append(
                    UTextComponent("§7 ❈ Tank Level: §l➡ §e${nf.format(tankLevel)}\n\n").setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        "§e${nf.format(tankXP)} XP"
                    )
                )
                .append("§a§l➜ Floor Completions:\n")


            val completionObj = cataData.tier_completions
            val highestFloor = cataData.highest_tier_completed.toInt()

            if (highestFloor != 0) {
                component.append(UTextComponent(" §aFloor Completions: §7(Hover)\n").setHoverText(buildString {
                    for (i in 0..highestFloor) {
                        append("§2§l●§a ")
                        append(if (i == 0) "Entrance: " else "Floor $i: ")
                        append("§e")
                        append(completionObj["$i"]!!.toInt())
                        append(if (i < highestFloor) "\n" else "")
                    }
                }))

                val fastestSTimes = cataData.fastest_time_s
                if (fastestSTimes.isNotEmpty()) {
                    component.append(UTextComponent(" §aFastest §2S §aCompletions: §7(Hover)\n").setHoverText(
                        buildString {
                            for (i in 0..highestFloor) {
                                append("§2§l●§a ")
                                append(if (i == 0) "Entrance: " else "Floor $i: ")
                                append("§e")
                                append(fastestSTimes["$i"]?.toDuration(DurationUnit.MILLISECONDS)?.timeFormat() ?: "§cNo S Completion")
                                append(if (i < highestFloor) "\n" else "")
                            }
                        }
                    ))
                }


                val fastestSPlusTimes = cataData.fastest_time_s_plus
                if (fastestSPlusTimes.isNotEmpty()) {
                    component.append(
                        UTextComponent(" §aFastest §2S+ §aCompletions: §7(Hover)\n\n").setHoverText(
                            buildString {
                                for (i in 0..highestFloor) {
                                    append("§2§l●§a ")
                                    append(if (i == 0) "Entrance: " else "Floor $i: ")
                                    append("§e")
                                    append(fastestSPlusTimes["$i"]?.toDuration(DurationUnit.MILLISECONDS)?.timeFormat() ?: "§cNo S+ Completion")
                                    append(if (i < highestFloor) "\n" else "")
                                }
                            }
                        )
                    )
                }
            }

            masterCataData?.tier_completions?.let { masterCompletionObj ->
                val highestMasterFloor = masterCataData.highest_tier_completed.toInt()

                if (masterCompletionObj.isNotEmpty() && highestMasterFloor != 0) {

                    component
                        .append("§a§l➜ Master Mode:\n")

                    component.append(UTextComponent(" §aFloor Completions: §7(Hover)\n").setHoverText(buildString {
                        for (i in 1..highestMasterFloor) {
                            append("§2§l●§a ")
                            append("Floor $i: ")
                            append("§e")
                            append(masterCompletionObj["$i"]?.toInt() ?: "§cDNF")
                            append(if (i < highestMasterFloor) "\n" else "")
                        }
                    }))


                    val masterFastestS = UTextComponent(" §aFastest §2S §aCompletions: §7(Hover)\n")

                    masterCataData.fastest_time_s.takeUnless(Map<*, *>::isEmpty)?.map { (floor, time) ->
                        "§2§l●§a Floor $floor: §e${time.toDuration(DurationUnit.MILLISECONDS).timeFormat()}"
                    }?.let { lines ->
                        val list = lines.toMutableList()
                        repeat(highestMasterFloor - lines.size - 1) { i ->
                            list += "§2§l●§a Floor $i: §e§cNo S Completion"
                        }
                        masterFastestS.setHoverText(list.joinToString("\n"))
                    } ?:  masterFastestS.setHoverText("§cNo S Completions")

                    component.append(masterFastestS)


                    val masterFastestSPlus = UTextComponent(" §aFastest §2S+ §aCompletions: §7(Hover)\n\n")

                    masterCataData.fastest_time_s_plus.takeUnless(Map<*, *>::isEmpty)?.map { (floor, time) ->
                        "§2§l●§a Floor $floor: §e${time.toDuration(DurationUnit.MILLISECONDS).timeFormat()}"
                    }?.let { lines ->
                        val list = lines.toMutableList()
                        repeat(highestMasterFloor - lines.size - 1) { i ->
                            list += "§2§l●§a Floor $i: §e§cNo S+ Completion"
                        }
                        masterFastestSPlus.setHoverText(list.joinToString("\n"))
                    } ?:  masterFastestSPlus.setHoverText("§cNo S+ Completions")

                    component.append(masterFastestSPlus)
                }

            }

            component
                .append("§a§l➜ Miscellaneous:\n")
                .append(" §aTotal Secrets Found: §l➡ §e${nf.format(secrets)}\n")
                .append(
                    " §aBlood Mobs Killed: §l➡ §e${
                        nf.format(
                            (profileData.player_stats.kills["watcher_summon_undead"]?.toInt() ?: 0) +
                                    (profileData.player_stats.kills["kills_master_watcher_summon_undead"]?.toInt() ?: 0)
                        )
                    }\n"
                )
                .chat()
        } catch (e: Throwable) {
            printMessage("$failPrefix §cCatacombs XP Lookup Failed: ${e.message ?: e::class.simpleName}")
            e.printStackTrace()
        }
    }

    private fun Duration.timeFormat() = toComponents { minutes, seconds, nanoseconds ->
        buildString {
            if (minutes > 0) {
                append(minutes)
                append(':')
            }
            append("%02d".format(seconds))
            if (nanoseconds != 0) {
                append('.')
                append("%03d".format((nanoseconds / 1e6).toInt()))
            }
        }
    }
}
