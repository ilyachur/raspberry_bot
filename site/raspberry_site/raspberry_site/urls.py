from django.conf.urls import patterns, include, url
from django.contrib import admin
from raspberry_home.views import index
from raspberry_home.views import cpu_temp_db

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'raspberry_site.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^$', index),
    url(r'^cpu_temp/$', cpu_temp_db),
)
