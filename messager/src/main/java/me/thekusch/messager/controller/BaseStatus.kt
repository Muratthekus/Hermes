package me.thekusch.messager.controller

public sealed interface BaseStatus {

    public object Initial: BaseStatus

    public interface WavingStarting: BaseStatus

    public interface WavingMatchDetecting: BaseStatus

    public object Talking: BaseStatus

    public object Disconnected: BaseStatus

    public object Dismissed: BaseStatus

}