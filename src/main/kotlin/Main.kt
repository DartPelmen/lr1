import org.lwjgl.BufferUtils
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL44.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL


import java.nio.FloatBuffer
import java.nio.ShortBuffer

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


fun main(args: Array<String>) {

    HelloWorld().run()

}


class HelloWorld {
    // The window handle
    private var window: Long = 0
    fun run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!")
        init()
        loop()

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    private fun init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(
            window
        ) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        }
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode!!.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)


        initData()
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.0f, .0f, 0.0f, 0.0f)



        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            glLoadIdentity()

            glTranslatef(posX, posY, posZ)
            glRotatef(angleX, 1f, 0f ,0f)
            glRotatef(angleX, 0f, 1f ,0f)
            glRotatef(angleX, 0f, 0f ,1f)

//            glBegin(GL_TRIANGLES);
//            glColor3f(1.0f, 0.0f, 0.0f);
//            glVertex3f(-0.6f, -0.4f, 0.0f);
//            glColor3f(0.0f, 1.0f, 0.0f);
//            glVertex3f(0.6f, -0.4f, 0.0f);
//            glColor3f(0.0f, 0.0f, 1.0f);
//            glVertex3f(0.0f, 0.6f, 0.0f);
//            glEnd();

            glEnableClientState(GL_VERTEX_ARRAY)
            glEnableClientState(GL_COLOR_ARRAY)
            glEnable(GL_CULL_FACE)

            glVertexPointer(3, GL_FLOAT, 0, bvertices)
            glColorPointer(3, GL_FLOAT, 0, bcolors)
            glDrawElements(GL_POLYGON, bindices)

            glfwSwapBuffers(window) // swap the color buffers

//            if (Mouse.isGrabbed()) {
//                val mouseDX: Float = Mouse.getDX() * mouseSpeed * 0.16f
//                val mouseDY: Float = Mouse.getDY() * mouseSpeed * 0.16f
//                if (rotation.y + mouseDX >= 360) {
//                    rotation.y = rotation.y + mouseDX - 360
//                } else if (rotation.y + mouseDX < 0) {
//                    rotation.y = 360 - rotation.y + mouseDX
//                } else {
//                    rotation.y += mouseDX
//                }
//                if (rotation.x - mouseDY >= maxLookDown
//                    && rotation.x - mouseDY <= maxLookUp
//                ) {
//                    rotation.x += -mouseDY
//                } else if (rotation.x - mouseDY < maxLookDown) {
//                    rotation.x = maxLookDown
//                } else if (rotation.x - mouseDY > maxLookUp) {
//                    rotation.x = maxLookUp
//                }
//            }

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }

    private var bvertices: FloatBuffer? = null
    private var bindices: ShortBuffer? = null
    private var bcolors: FloatBuffer? = null

    private var posX = 0.0f
    private var posY = 0.0f
    private var posZ = 0.0f

    private var angleX = 0.0f
    private var angleY = 0.0f
    private var angleZ = 0.0f

    private fun initData() {
        val vertices = ArrayList<Float>()
        val indices = ArrayList<Short>()
        val colors = ArrayList<Float>()

        val segments = 10
        val h = PI / (segments - 1)
        val r = 0.5f

        for (i in 0..segments ) {
            val a = i * h
            val y = sin(a).toFloat()
            val x = cos(a).toFloat()
            vertices.add(x * r)
            vertices.add(y * r)
            vertices.add(0.0f)
            indices.add(i.toShort())

            colors.add(tan(a.toFloat()))
            colors.add(cos(a.toFloat()))
            colors.add(sin(a.toFloat()))
        }

        bvertices = BufferUtils.createFloatBuffer(vertices.size)
        bindices = BufferUtils.createShortBuffer(indices.size)
        bcolors = BufferUtils.createFloatBuffer(colors.size)

        for (f in vertices) {
            bvertices?.put(f)
        }
        val vertexCount: Int = vertices.size / 3

        for (sh in indices) {
            bindices?.put((sh % vertexCount).toShort())
        }

        for (c in colors){
            bcolors?.put(c);
        }

        bvertices?.flip()
        bindices?.flip()
        bcolors?.flip()
    }
}