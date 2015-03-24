type InvokeRequest:void {
	.operation:string
	.outputPort:string
	.resourcePath?:string
	.data?:undefined
}

/**!
WARNING: the API of this service is experimental. Use it at your own risk.
*/
interface ReflectionIface {
RequestResponse:
	invoke(InvokeRequest)(undefined) throws OperationNotFound(string) InvocationFault(undefined)
}

outputPort Reflection {
Interfaces: ReflectionIface
}

embedded {
Java:
	"joliex.lang.reflection.Reflection" in Reflection
}