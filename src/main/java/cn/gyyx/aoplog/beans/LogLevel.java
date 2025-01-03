package cn.gyyx.aoplog.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogLevel {

    LeastImportant(1),NotImportant(2),Normal(3),Important(4),VeryImportant(5);

    private Integer logLevel;
}
