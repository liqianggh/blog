---
title: "我的VuePress试验田"
date: "2021-07-13"
description: "VuePress测试"
---
##  扩展语法
###  链接
代码：
```md
[首页](/)
[JMH使用教程](./jmh-tutorial.md)
[MySQL](../mysql/)
[MySQL-1](../mysql/mysql-index-1.md)
[外部链接](http://www.baidu.com)
```
示例：
[首页](/)
<br/>[JMH使用教程](./jmh-tutorial.md)
<br/>[MySQL](../mysql/)
<br/>[MySQL-1](../mysql/mysql-index-1.md)
<br/>[外部链接](http://www.baidu.com)

### 目录
代码：
```md
[[toc]]
```
示例：
[[toc]]

### 自定义容器
```md
::: tip
这是一个提示
:::

::: warning
这是一个警告
:::

::: danger
这是一个危险警告
:::

::: details
这是一个详情块，在 IE / Edge 中不生效
:::
```
示例：

::: tip 温馨提示
这是一个提示
:::

::: warning 注意事项
这是一个警告
:::

::: danger 危险告警
这是一个危险警告
:::

::: details
这是一个详情块，在 IE / Edge 中不生效
:::

::: details 点击查看代码
```js
console.log('你好，VuePress！')
```
:::

### 代码中的亮行
代码：
```md
``` js {4}
export default {
  data () {
    return {
      msg: 'Highlighted!'
    }
  }
}
``` 
```
示例：
``` js {4}
export default {
  data () {
    return {
      msg: 'Highlighted!'
    }
  }
}
``` 
### 标题主题 <Badge text="beta" type="warning"/> <Badge text="默认主题"/>
代码: 
  ```md
## 标题主题 <Badge text="beta" type="warning"/> <Badge text="默认主题"/>
  ```

属性:
  * `text`-string
  * `type`-string, 可选值 "tip"|"warning"|"error"，默认值是： "tip"
  * `veritcal`-string, 可选值： "top"|"middle"，默认值是： "top"

示例

```md
## 标题主题 <Badge text="beta" type="warning"/> <Badge text="默认主题"/>
```

# 一级标题

## 二级标题

### 三级标题

#### 四级标题
```md
# 一级标题

## 二级标题

### 三级标题

### 四级标题
```