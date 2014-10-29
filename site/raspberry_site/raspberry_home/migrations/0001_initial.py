# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='CPUTemperature',
            fields=[
                ('datetime', models.DateTimeField(serialize=False, primary_key=True)),
                ('temperature', models.FloatField()),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
