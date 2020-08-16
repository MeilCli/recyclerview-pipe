package net.meilcli.pipe.internal

import net.meilcli.pipe.INotifyPipeChanged
import net.meilcli.pipe.IPipeEventRegistry
import net.meilcli.pipe.PipeEvent

internal interface INotifyPipeChangedContainer : IPipeEventRegistry {

    val eventNotifiers: MutableList<INotifyPipeChanged>

    override fun registerNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
        if (eventNotifiers.contains(notifyPipeChanged)) {
            return
        }
        eventNotifiers.add(notifyPipeChanged)
    }

    override fun unregisterNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged) {
        eventNotifiers.remove(notifyPipeChanged)
    }

    fun raiseEvent(event: PipeEvent) {
        for (eventNotifier in eventNotifiers) {
            eventNotifier.eventRaised(event)
        }
    }
}