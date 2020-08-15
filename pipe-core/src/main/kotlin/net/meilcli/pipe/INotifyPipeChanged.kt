package net.meilcli.pipe

interface INotifyPipeChanged {

    fun eventRaised(event: PipeEvent)
}