package com.app.nav

import androidx.compose.runtime.Composable
import com.app.data.Event
import com.app.data.EventType
import com.app.data.Prize
import com.app.data.Student
import com.app.ui.*
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable


interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun navigateToLists(name : String)

    sealed class Child {
        class StudentListChild(val component: StudentListComponent) : Child()
        class StudentDetailsChild(val component: StudentDetailsComponent) : Child()
        class StudentDetailsInsertChild(val component : StudentDetailsInsertComponent) : Child()
        class EventListChild(val component: EventListComponent) : Child()
        class EventDetailsChild(val component : EventDetailsComponent) : Child()
//        class EventDetailsInsertChild(val component: EventDetailsInsertComponent) : Child()
//        class PrizeListChild(val component: PrizeListComponent) : Child()
//        class PrizeDetailsChild(val component : PrizeDetailsComponent) : Child()
//        class PrizeDetailsInsertChild(val component : PrizeDetailsInsertComponent) : Child()
    }
}


class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.StudentList,
            handleBackButton = true,
            childFactory = ::child
        )


    override fun navigateToLists(name : String) {
        when (name) {
            "Students" -> navigation.replaceAll(Config.StudentList)
             "Events" -> navigation.replaceAll(Config.EventList)
//             "Prizes" -> navigation.replaceAll(Config.PrizesList)
        }
    }

    override val stack: Value<ChildStack<*, RootComponent.Child>> =_stack

    private fun child(config : Config, componentContext: ComponentContext) : RootComponent.Child =
        when (config) {
            is Config.StudentList -> RootComponent.Child.StudentListChild(studentListComponent(componentContext))
            is Config.StudentDetails -> RootComponent.Child.StudentDetailsChild(studentDetailsComponent(componentContext, config))
            is Config.StudentDetailsInsert -> RootComponent.Child.StudentDetailsInsertChild(insertStudentDetailsComponent(componentContext, config))
            is Config.EventList -> RootComponent.Child.EventListChild(eventListComponent(componentContext, config))
            is Config.EventDetails -> RootComponent.Child.EventDetailsChild(eventDetailsComponent(componentContext, config))
//            is Config.EventDetailsInsert -> RootComponent.Child.EventDetailsInsertChild(insertEventDetailsComponent(componentContext, config))
//            is Config.PrizeList -> RootComponent.Child.PrizeListChild(prizeListComponent(componentContext, config))
//            is Config.PrizeDetails -> RootComponent.Child.PrizeDetailsChild(prizeDetailsComponent(componentContext, config))
//            is Config.PrizeDetailsInsert -> RootComponent.Child.PrizeDetailsInsertChild(insertPrizeDetailsComponent(componentContext, config))
        }


    private fun studentListComponent(componentContext: ComponentContext) : StudentListComponent =
        DefaultStudentListComponent(
            componentContext = componentContext,
            onStudentSelected = {student: Student ->
                navigation.push(Config.StudentDetails(student = student))
            },
            onAddStudentClicked = {
                navigation.push(Config.StudentDetailsInsert)
            }
        )



    private fun studentDetailsComponent(componentContext: ComponentContext, config: Config.StudentDetails) : StudentDetailsComponent =
        DefaultStudentDetailsComponent(
            componentContext = componentContext,
            student = config.student,
            onFinished = navigation::pop
        )
//
    private fun insertStudentDetailsComponent(componentContext: ComponentContext, config : Config.StudentDetailsInsert) : StudentDetailsInsertComponent =
        DefaultStudentDetailsInsertComponent(
            componentContext = componentContext,
            onFinished = navigation::pop
        )
//
    private fun eventListComponent(componentContext: ComponentContext, config : Config.EventList) : EventListComponent =
        DefaultEventListComponent(
            componentContext = componentContext,
            onEventSelected = {event: Event, types : List<EventType> ->
                navigation.push(Config.EventDetails(event= event, types = types))
            },
            //onAddEventClicked = {types : List<EventType> ->
            //    navigation.push(Config.EventDetailsInsert(types = types))}
        )
//
    private fun eventDetailsComponent(componentContext: ComponentContext, config : Config.EventDetails) : EventDetailsComponent =
        DefaultEventDetailsComponent(
            componentContext = componentContext,
            event = config.event,
            onFinished = navigation::pop,
            types = config.types
        )
//
//    private fun insertEventDetailsComponent(componentContext: ComponentContext, config : Config.EventDetailsInsert) : EventDetailsInsertComponent =
//        DefaultEventDetailsInsertComponent(
//            componentContext = componentContext,
//            onFinished = navigation::pop,
//            types = config.types
//        )
//
//    private fun prizeListComponent(componentContext: ComponentContext, config : Config.PrizeList) : PrizeListComponent =
//        DefaultPrizeListComponent(
//            componentContext = componentContext,
//            onPrizeSelected = {prize: Prize ->
//                navigation.push(Config.PrizeDetails(prize = prize))
//            }
//        )
//
//    private fun prizeDetailsComponent(componentContext: ComponentContext, config : Config.PrizeDetails) : PrizeDetailsComponent =
//        DefaultPrizeDetailsComponent(
//            componentContext = componentContext,
//            prize = config.prize,
//            onFinished = navigation::pop
//        )
//
//    private fun insertPrizeDetailsComponent(componentContext: ComponentContext, config : Config.PrizeDetailsInsert) : PrizeDetailsInsertComponent =
//        DefaultPrizeDetailsInsertComponent(
//            componentContext = componentContext,
//            onFinished = navigation::pop
//        )


    private sealed interface Config : Parcelable {
        object StudentList : Config
        data class StudentDetails(val student: Student) : Config
        object StudentDetailsInsert: Config
        object EventList : Config
        data class EventDetails(val event : Event, val types : List<EventType>) : Config
//        data class EventDetailsInsert(val types : List<EventType>) : Config
//        object PrizeList : Config
//        data class PrizeDetails(val prize: Prize) : Config
//        object PrizeDetailsInsert : Config
    }

}