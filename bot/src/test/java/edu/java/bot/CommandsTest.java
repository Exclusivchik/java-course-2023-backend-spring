//package edu.java.bot;
//
//import com.pengrad.telegrambot.request.SendMessage;
//import edu.java.bot.commands.Command;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class CommandsTest {
//    private static final long CHAT_ID = 10L;
//    @Autowired
//    Command helpCommand;
//    @Autowired
//    Command listCommand;
//    @Autowired
//    Command startCommand;
//    @Autowired
//    Command trackCommand;
//    @Autowired
//    Command untrackCommand;
//
//
//    @Test
//    public void shouldReturnCorrectAnswersFromStartCommandHandler() {
//        SendMessage firstAnswer = startCommand.handle(CHAT_ID, "/start");
//        SendMessage secondAnswer = startCommand.handle(CHAT_ID, "/start");
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                10L,
//                firstAnswer.getParameters().get("chat_id")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы успешно зарегистрировались",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы уже зарегистрированы",
//                secondAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromHelpCommandHandler() {
//        SendMessage firstAnswer = helpCommand.handle(CHAT_ID, "/help");
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                """
//                    /untrack - прекратить отслеживать ресурс
//                    /track - начать отслеживать ресурс
//                    /start - регистрация пользователя
//                    /list - Список подписок
//                    """,
//                firstAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromListCommandHandler() throws Exception {
//        SendMessage firstAnswer = listCommand.handle(CHAT_ID, "/list");
//
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Заглушка для listCommand",
//                firstAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromTrackCommandHandler() {
//        SendMessage firstAnswer =
//            trackCommand.handle(CHAT_ID, "/track https://github.com/Exclusivchik");
//        SendMessage secondAnswer =
//            trackCommand.handle(CHAT_ID, "/track abacaba");
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Ссылка успешно добавлена",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Проверьте корректность ссылки",
//                secondAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromUntrackCommandHandler() throws Exception {
//        SendMessage firstAnswer = untrackCommand.handle(CHAT_ID, "/untrack https://github.com/Exclusivchik");
//        SendMessage secondAnswer = untrackCommand.handle(CHAT_ID, "/untrack dsasdsfdd");
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Вы успешно отписались от ресурса",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Проверьте корректность ссылки",
//                secondAnswer.getParameters().get("text")
//            )
//        );
//    }
//}
