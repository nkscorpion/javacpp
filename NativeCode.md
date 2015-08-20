# Introduction #
Native code must be thread-safe and it must be written for compilation into a shared library. Javacpp provides wrapper code for your C++ to spare you the pain of arcane JNI syntax, but your C++ is running in the JNI environment.

# JNI Reminders #
  * There is only one JVM handle per process. All threads running in one process use the same JVM. It is good practice to store this handle in native code one time and use it for all native call invocations for the duration of the process.
  * There is one JNIEnv handle _per thread_. Using a JNIEnv handle for a different thread will crash the JNI. If your application invokes native code to set up processing and then subsequently invokes native methods, there is no guarantee that subsequent calls to the native library will use the same thread. Therefore, never save a reference to a JNIEnv handle for use in subsequent method invocations.
  * If native code spawns threads that are expected to interact with the JVM then these threads must be associated with the JVM when they are started and they must be dissociated from the thread just before it is stopped. If this is not done the JNI environment will become unstable and your application will crash. **Beware multiple exit points and exceptions, every exit path must be covered.**


> # Calling Java from C++ Without Callbacks #
> In order to make Java available from within C++ without using callbacks it is
> necessary to store a pointer to the Java Virtual Machine. Javacpp provides
> means to do this by adding a putEnv() method in Java and a corresponding
> method to access it in the C++.
> ## Java Interface Class ##
> As one of the public static native methods define putEnv:
```
 public static native @Raw(withEnv=true) void putEnv();
```
> ## C++ Header ##
> For the case of no access to Java from C++ nothing special is needed in the
> header. However, if it is desired to access Java from C++ without using
> callbacks then it is necessary to get a reference to the JVM which will then
> be used to get a JNIEnv pointer whenever Java access is required.

> The top-level library header this code will provide storage for the JVM
> accessible to all C++ source files:
```
 namespace cprg
{

#ifdef USING_JAVA_INT
	static JavaVM * jvm;
#endif

 ...
 } // Namespace
 == Initialize C++ Calling Java ==
```
> Then somewhere in the initialization code before the JVM pointer is needed:
```
	// Local variables not saved in persistent storage
	JNIEnv * je; 
	jclass * jc;
	// Call to Javacpp to retrieve the JNIEnv for this thread
	putEnv(je, jc);
	je->GetJavaVM(&jvm); // Pointer to JVM stored in top-level global
```

## C++ to Java Call ##
After initializing global variable _jvm_ it may then be used in various ways
to get an appropriate JNIEnv pointer (assume **JNIEnv \_env_ in the appropriate
non-global scope):
```
		//$$$$ Starting a new thread on the C++ side, the jniEnv is valid for
		// the entire execution of the thread.
		int res = jvm->AttachCurrentThread((void**)&jniEnv, NULL);
		if(res<0)
		{
			std::cout << "***AttachCurrentThread failed with code " << res << std::endl;
			status = false;
		}
		if(NULL == jniEnv)
		{
			std::cout << "***AttachCurrentThread: Returned jniEnv is NULL" << std::endl;
			status = false;
		}
		// *remember to call DetachCurrentThread before the thread exits!*
		//----- end new thread example
		
		//$$$$ In a new invocation of a JNI function where the thread is 
		// not known (i.e. all subsequent invocations after the 
		// invocation to set the Java Virtual Machine pointer)
		
		int res = jvm->GetEnv((void **)&env, JNI_VERSION_2_0);
		// Note that env will be valid for the current invocation. Do not store
		// this value for subsequent invocations. Constant JNI_VERSION_2_0 
		// should be replaced with the one appropriate to your JVM.
		
		//----- end new invocation example
```
Once Env pointer is obtained once it may be used for the entire invocation.
An example of calling a method follows, without any error checking for clarity:
```
	clsIfc = jniEnv->FindClass("tps/StuffInter");
	jmethodID getFactor = jniEnv->GetStaticMethodID(clsIfc, "getFactor","()I");

	// Perform the callback.
	int retval = jniEnv->CallStaticIntMethod(clsIfc,getFactor);
```
Note that under certain conditions _clsIfc_ may be stored persistently to save
calling overhead. For details see the JNI documentation. This example gets a
fresh value of _clsIfc_ on each call to ensure that it is valid for the
thread in which it is executing.**
