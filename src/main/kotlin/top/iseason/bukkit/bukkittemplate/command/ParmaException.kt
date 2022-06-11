package top.iseason.bukkit.bukkittemplate.command


class ParmaException(val arg: String, val typeParam: TypeParam<*>? = null) : Exception(arg)