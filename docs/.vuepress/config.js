/**
 * @see https://vuepress.vuejs.org/zh/
 */
module.exports = {
  port: '4000',
  dest: 'dist',
  base: '/',
  title: 'Java填坑笔记',
  description: 'Java 填坑记录',
  head: [['link', { rel: 'icon', href: `/favicon.ico` }]],
  markdown: {
    externalLinks: {
      target: '_blank',
      rel: 'noopener noreferrer',
    },
  },
  themeConfig: {
    logo: 'https://github.com/liqianggh/blog/blob/master/docs/.vuepress/public/images/javaTKBJ-1.jpeg?raw=true',
    repo: 'liqianggh/blog',
    repoLabel: 'Github',
    docsDir: 'docs',
    docsBranch: 'master',
    editLinks: true,
    smoothScroll: true,
    locales: {
      '/': {
        label: '简体中文',
        selectText: 'Languages',
        editLinkText: '帮助我们改善此页面！',
        lastUpdated: '上次更新',
        nav: [
          {
            text: 'MySQL',
            link: '/mysql/',
          },
          {
            text: '测试',
            link: '/test/',
          },
          // {
          //   text: '✨ Java系列',
          //   ariaLabel: 'Java',
          //   items: [
          //     {
          //       text: 'Java 教程 📚',
          //       link: 'https://liqianggh.github.io/java-tutorial/',
          //       target: '_blank',
          //       rel: '',
          //     },
          //     {
          //       text: 'JavaCore 教程 📚',
          //       link: 'https://liqianggh.github.io/javacore/',
          //       target: '_blank',
          //       rel: '',
          //     },
          //     {
          //       text: 'JavaTech 教程 📚',
          //       link: 'https://liqianggh.github.io/JavaTKBJ/',
          //       target: '_blank',
          //       rel: '',
          //     },
          //     {
          //       text: 'Spring 教程 📚',
          //       link: 'https://liqianggh.github.io/spring-tutorial/',
          //       target: '_blank',
          //       rel: '',
          //     },
          //     {
          //       text: 'Spring Boot 教程 📚',
          //       link: 'https://liqianggh.github.io/spring-boot-tutorial/',
          //       target: '_blank',
          //       rel: '',
          //     },
          //   ],
          // },
          // {
          //   text: '🎯 博客',
          //   link: 'https://www.mycookies.cn',
          //   target: '_blank',
          //   rel: '',
          // },
        ],
        sidebar: 'auto',
        sidebarDepth: 2,
      },
    },
  },
  plugins: [
    [
      '@vuepress/active-header-links',
      {
        sidebarLinkSelector: '.sidebar-link',
        headerAnchorSelector: '.header-anchor',
      },
    ],
    ['@vuepress/back-to-top', true],
    [
      '@vuepress/pwa',
      {
        serviceWorker: true,
        updatePopup: true,
      },
    ],
    [
      '@vuepress/last-updated',
      {
        transformer: (timestamp, lang) => {
          // 不要忘了安装 moment
          const moment = require('moment')
          moment.locale(lang)
          return moment(timestamp).fromNow()
        },
      },
    ],
    ['@vuepress/medium-zoom', true],
    [
      'container',
      {
        type: 'vue',
        before: '<pre class="vue-container"><code>',
        after: '</code></pre>',
      },
    ],
    [
      'container',
      {
        type: 'upgrade',
        before: (info) => `<UpgradePath title="${info}">`,
        after: '</UpgradePath>',
      },
    ],
    ['flowchart'],
  ],
}
