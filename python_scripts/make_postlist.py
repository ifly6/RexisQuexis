#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Sep 12 ... 2021
@author: ifly6
"""
import re
import time
from collections import namedtuple

import requests
import pandas as pd
from bs4 import BeautifulSoup

Post = namedtuple('Post', ['title', 'post', 'repealed', 'is_empty'])
url = 'https://forum.nationstates.net/viewtopic.php?f=9&t=30&start={}'

# create containers and helper functions for parsing
seen_list = []
titles_and_posts = []


def is_repealed(post_title):
    post_text = str(post_title.parent.parent)

    # this isn't always marked properly... but it should be dispositive if marked
    if '[REPEALED]' in post_title.text:
        if '<del>' in post_text:
            return True
        else:
            # shouldn't ever happen, check for consistency!
            raise RuntimeError(f'post {i} marked as repealed but does not have strike-throughs!')

    # strikethrough is present only in repealed resolutions
    if '<del>' in post_text and '</del>' in post_text:
        if 'struck out by' in post_text.lower() or 'repealed by' in post_text.lower():
            return True

        else:
            # this also shouldn't ever happen, do manual check for consistency...
            raise RuntimeError(f'post {i} contains del tags but no "repealed by" tags!')

    return False


def is_empty(i):
    post_text = post_title.parent.parent.select('div.content')[0].text.strip()

    # normal posts should never start with a full stop
    if (post_text.startswith('.') or len(post_text) == 0):
        return True

    return False


# parse the raw forum data
no_more = False
for i in range(0, 40):
    if no_more is True:
        break

    adj_value = i * 25
    adj_url = url.format(adj_value)
    print(f'getting posts {i}')

    soup = BeautifulSoup(requests.get(adj_url).text, 'lxml')
    for post_title in soup.select('div.postbody h3 a'):
        if post_title['href'] in ['#p309', '#p310']:
            # don't attempt to parse the intro and table!
            continue

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

# create data frame for tabular repr
posts = pd.DataFrame(titles_and_posts)

resolutions = posts.copy()  # manual exclude rexis intro and contents posts
resolutions['code'] = resolutions.apply(
    lambda r: '[post={}]{}[/post]'.format(r['post'], r['title']), axis=1)  # make post bbcode

# shift using original index to pages
resolutions['page'] = (resolutions.index // 25) + 1

# create page boundaries then for each page, generate and append links
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

# save to var and print
contents = '\n'.join(lines) + '[/list]'
print(contents)

# Nb! if using var explorer in spyder, open the string in that and copy it all for pasting!
