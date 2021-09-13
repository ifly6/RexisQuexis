import re
import time
from collections import namedtuple
import pyperclip

import requests
import pandas as pd
from bs4 import BeautifulSoup

Post = namedtuple('Post', ['title', 'post', 'repealed', 'is_empty'])
url = 'https://forum.nationstates.net/viewtopic.php?f=9&t=30&start={}'

seen_list = []
titles_and_posts = []


def is_repealed(i):
    if '[REPEALED]' in post_title.text:
        return True

    post_text = str(i.parent.parent)
    if ('<del>' in post_text and '</del>' in post_text
            and ('struck out by' in post_text.lower() or 'repealed by' in post_text.lower())):
        return True

    return False


def is_empty(i):
    post_text = post_title.parent.parent.select('div.content')[0].text.strip()
    if (post_text == '.'
            or len(post_text) == 0):
        return True

    return False


no_more = False
for i in range(0, 40):
    if no_more is True:
        break

    adj_value = i * 25
    adj_url = url.format(adj_value)
    print(f'getting posts {i}')

    soup = BeautifulSoup(requests.get(adj_url).text, 'lxml')
    for post_title in soup.select('div.postbody h3 a'):
        if post_title['href'] in seen_list:
            no_more = True
            break

        try:
            text = post_title.parent.parent.select('div.content span')[0].text
        except IndexError:
            text = ''

        titles_and_posts.append(Post(
            text,
            re.search(r'\d+', post_title['href'])[0],
            is_repealed(post_title),
            is_empty(post_title)))
        seen_list.append(post_title['href'])

    time.sleep(5)

posts = pd.DataFrame(titles_and_posts)

resolutions = posts.loc[2:].copy()
resolutions['code'] = resolutions.apply(
    lambda r: '[post={}]{}[/post]'.format(r['post'], r['title']), axis=1)

resolutions['page'] = (resolutions.index // 25) + 1

lines = ['[list=1][b][color=darkblue]Page 1[/color][/b]', '']
for g, df in resolutions.groupby('page'):
    if g != 1:
        lines.append('')
        lines.append(f'[b][color=darkblue]Page {g}[/b][/color]')
        lines.append('')
        lines.append('')

    for i, r in df.iterrows():
        if r['is_empty'] == False:
            lines.append('[*]' + r['code'] +
                         (' [b][REPEALED][/b]' if r['repealed'] else ''))

contents = '\n'.join(lines) + '[/list]'
