from django.db import models

# Create your models here.
class CPUTemperature(models.Model):
    datetime = models.DateTimeField(primary_key=True)
    temperature = models.FloatField()

    def __unicode__(self):
        return self.temperature