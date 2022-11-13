package fr.pickaria.economy

@RequiresOptIn(message = "This API is not recommended to use, prefer implementing CurrencyExtensions.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class GlobalCurrencyExtensions
