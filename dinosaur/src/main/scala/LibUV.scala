package io.dinosaur
import scalanative.native._

@link("uv")
@extern
object LibUV {
  type PipeHandle   = Ptr[Byte]
  type Loop         = Ptr[Byte]
  type Buffer       = CStruct2[Ptr[Byte], CSize]
  type WriteReq     = Ptr[Ptr[Byte]]
  type ShutdownReq  = Ptr[Ptr[Byte]]
  type Connection   = Ptr[Byte]
  type ConnectionCB = CFunctionPtr2[PipeHandle, Int, Unit]
  type AllocCB      = CFunctionPtr3[PipeHandle, CSize, Ptr[Buffer], Unit]
  type ReadCB       = CFunctionPtr3[PipeHandle, CSSize, Ptr[Buffer], Unit]
  type WriteCB      = CFunctionPtr2[WriteReq, Int, Unit]
  type ShutdownCB   = CFunctionPtr2[ShutdownReq, Int, Unit]
  type CloseCB      = CFunctionPtr1[PipeHandle, Unit]

  def uv_default_loop(): Loop                                                                                  = extern
  def uv_loop_size(): CSize                                                                                    = extern
  def uv_handle_size(h_type: Int): CSize                                                                       = extern
  def uv_req_size(r_type: Int): CSize                                                                          = extern
  def uv_pipe_init(loop: Loop, handle: PipeHandle, ipcFlag: Int): Unit                                         = extern
  def uv_pipe_bind(handle: PipeHandle, socketName: CString): Int                                               = extern
  def uv_listen(handle: PipeHandle, backlog: Int, callback: ConnectionCB): Int                                 = extern
  def uv_accept(server: PipeHandle, client: PipeHandle): Int                                                   = extern
  def uv_read_start(client: PipeHandle, allocCB: AllocCB, readCB: ReadCB): Int                                 = extern
  def uv_write(writeReq: WriteReq, client: PipeHandle, bufs: Ptr[Buffer], numBufs: Int, writeCB: WriteCB): Int = extern
  def uv_read_stop(client: PipeHandle): Int                                                                    = extern
  def uv_shutdown(shutdownReq: ShutdownReq, client: PipeHandle, shutdownCB: ShutdownCB): Int                   = extern
  def uv_close(handle: PipeHandle, closeCB: CloseCB): Unit                                                     = extern
  def uv_is_closing(handle: PipeHandle): Int                                                                   = extern
  def uv_run(loop: Loop, runMode: Int): Int                                                                    = extern
}

/*
struct sockaddr_in

uv_loop_t
uv_write_t
uv_tcp_t
uv_stream_t
uv_buf_t
uv_handle_t

uv_default_loop
uv_tcp_init
uv_ip4_addr
uv_tcp_bind
uv_listen
uv_run
uv_tcp_init
uv_accept
uv_read_start
uv_write
uv_strerror
uv_close
 */
