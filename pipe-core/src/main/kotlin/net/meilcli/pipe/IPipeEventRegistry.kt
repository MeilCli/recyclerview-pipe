package net.meilcli.pipe

interface IPipeEventRegistry {

    fun registerNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged)

    fun unregisterNotifyPipeChanged(notifyPipeChanged: INotifyPipeChanged)
}