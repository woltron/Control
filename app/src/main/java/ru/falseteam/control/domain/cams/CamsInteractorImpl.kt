package ru.falseteam.control.domain.cams

import kotlinx.coroutines.flow.Flow
import ru.falseteam.control.api.CamsApi
import ru.falseteam.control.api.dto.CameraDTO
import ru.falseteam.control.api.dto.CameraStatusDTO
import ru.falseteam.control.api.rsub.CamsRSub
import ru.falseteam.control.api.rsub.CamsStatusRSub
import ru.falseteam.control.domain.servers.ServersInteractor

class CamsInteractorImpl(
    private val camsRSub: CamsRSub,
    private val camsStatusRSub: CamsStatusRSub,
    private val camsApi: CamsApi,
    private val serversInteractor: ServersInteractor,
) : CamsInteractor {
    override fun observeAll(): Flow<List<CameraDTO>> = camsRSub.observeAll()
    override fun observerStatus(): Flow<Map<Long, CameraStatusDTO>> = camsStatusRSub.observeAll()

    override suspend fun put(camera: CameraDTO) =
        camsApi.addCamera(serversInteractor.getPrimaryServer(), camera)

    override fun observeVideoStream(id: Long): Flow<ByteArray> = camsApi.getVideoStream(id)
}