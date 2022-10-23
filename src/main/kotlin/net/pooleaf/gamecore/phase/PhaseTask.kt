package net.pooleaf.gamecore.phase

import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class PhaseTask(
    val phasePipeline: PhasePipeline
) {

    var bukkitTask: BukkitTask? = null


    fun start() {
        if (bukkitTask == null) {
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(GameCore.gamePlugin as JavaPlugin, Runnable {
                val currentPhase = phasePipeline.getCurrentPhase()

                // 현재 Phase 없으면 중단
                if (currentPhase == null) {
                    cancel()
                    return@Runnable
                }

                // 현재 Phase 진행
                if (!currentPhase.started) {
                    currentPhase.start()
                } else {
                    currentPhase.run()
                }

                // 현재 Phase가 끝나면 다음 Phase 시작
                if (currentPhase.ended) {
                    val newPhase = phasePipeline.nextPhase()
                    if (newPhase == null) {
                        cancel()
                    } else {
                        newPhase.start()
                    }
                }
            }, 0L, 20L)
        }
    }

    fun cancel() {
        bukkitTask?.cancel()
        bukkitTask = null
    }

}