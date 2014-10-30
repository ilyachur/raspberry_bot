from datetime import datetime
from django.shortcuts import render
from django.http import JsonResponse
from django.template import loader, Context
from django.shortcuts import render_to_response
from django.http import HttpResponse

from reportlab.graphics.shapes import Drawing, String
from reportlab.graphics.charts.lineplots import LinePlot
from reportlab.graphics.charts.lineplots import ScatterPlot
from reportlab.lib import colors
from reportlab.graphics.charts.legends import Legend
from reportlab.graphics.charts.textlabels import Label
from reportlab.graphics.widgets.markers import makeMarker
from raspberry_home.models import CPUTemperature


class MyLineChartDrawing(Drawing):
    def __init__(self, width=600, height=400, *args, **kw):
        apply(Drawing.__init__, (self, width, height) + args, kw)
        self.add(LinePlot(), name='chart')

        self.add(String(200, 180, 'Hello World'), name='title')

        # set any shapes, fonts, colors you want here.  We'll just
        # set a title font and place the chart within the drawing.
        #pick colors for all the lines, do as many as you need
        self.chart.x = 20
        self.chart.y = 30
        self.chart.width = self.width - 100
        self.chart.height = self.height - 75
        self.chart.lines[0].strokeColor = colors.blue
        self.chart.lines[1].strokeColor = colors.green
        self.chart.lines[2].strokeColor = colors.yellow
        self.chart.lines[3].strokeColor = colors.red
        self.chart.lines[4].strokeColor = colors.black
        self.chart.lines[5].strokeColor = colors.orange
        self.chart.lines[6].strokeColor = colors.cyan
        self.chart.lines[7].strokeColor = colors.magenta
        self.chart.lines[8].strokeColor = colors.brown
        self.chart.fillColor = colors.white
        self.title.fontName = 'Times-Roman'
        self.title.fontSize = 18
        self.chart.data = [((0, 50), (100, 100), (200, 200), (250, 210), (300, 300), (400, 500))]
        self.chart.xValueAxis.labels.fontSize = 12
        self.chart.xValueAxis.forceZero = 0
        self.chart.xValueAxis.gridEnd = 115
        self.chart.xValueAxis.tickDown = 3
        self.chart.xValueAxis.visibleGrid = 1
        self.chart.yValueAxis.tickLeft = 3
        self.chart.yValueAxis.labels.fontName = 'Times-Roman'
        self.chart.yValueAxis.labels.fontSize = 12
        self.title.x = self.width / 2
        self.title.y = 0
        self.title.textAnchor = 'middle'
        self.add(Legend(), name='Legend')
        self.Legend.fontName = 'Times-Roman'
        self.Legend.fontSize = 12
        self.Legend.x = self.width
        self.Legend.y = 85
        self.Legend.dxTextSpace = 5
        self.Legend.dy = 5
        self.Legend.dx = 5
        self.Legend.deltay = 5
        self.Legend.alignment = 'right'
        self.add(Label(), name='XLabel')
        self.XLabel.fontName = 'Times-Roman'
        self.XLabel.fontSize = 12
        self.XLabel.x = 85
        self.XLabel.y = 5
        self.XLabel.textAnchor = 'middle'
        #self.XLabel.height = 20
        self.XLabel._text = ""
        self.add(Label(), name='YLabel')
        self.YLabel.fontName = 'Times-Roman'
        self.YLabel.fontSize = 12
        self.YLabel.x = 2
        self.YLabel.y = 80
        self.YLabel.angle = 90
        self.YLabel.textAnchor = 'middle'
        self.YLabel._text = ""
        self.chart.yValueAxis.forceZero = 1
        self.chart.xValueAxis.forceZero = 1


"""
class MyBarChartDrawing(Drawing):
    def __init__(self, width=400, height=200, *args, **kw):
        Drawing.__init__(self,width,height,*args,**kw)
        self.add(GridLinePlot(), name='chart')

        self.add(String(200,180,'Hello World'), name='title')

        #set any shapes, fonts, colors you want here.  We'll just
        #set a title font and place the chart within the drawing
        self.chart.x = 20
        self.chart.y = 20
        self.chart.width = self.width - 20
        self.chart.height = self.height - 40

        self.title.fontName = 'Helvetica-Bold'
        self.title.fontSize = 12

        self.chart.data = [[(20010630, 100), (20011231, 101), (20020630, 100.05), (20021231, 102), (20030630, 103), (20031230, 104), (20040630, 99.200000000000003), (20041231, 99.099999999999994)]]
"""


# Create your views here.
def index(request):
    data = cpu_temp_db()
    # if request.user.is_authenticated():
    t = loader.get_template("index.html")
    print(data['data'])
    c = Context({'data_temp': data['data'], 'proc_temp': data['temp']})
    return HttpResponse(t.render(c))


gl_data = [[1, 1], [2, 2], [2.5, 1], [3, 3], [4, 5]]
gl_index = 5
big_data = []
int_count = 0


def linechart(request):
    global big_data
    global int_count
    for str_temp in CPUTemperature.objects.all():
        big_data.append([int_count, float(str(str_temp).split(' ')[1])])
        int_count += 1
    # if gl_index < 0:
    # gl_index = 5
    #big_data.append([int_count, float(gl_data[gl_index])])
    #gl_index -= 1
    #instantiate a drawing object
    d = MyLineChartDrawing()

    #extract the request params of interest.
    #I suggest having a default for everything.


    d.height = 300
    d.chart.height = 250

    d.width = 700
    d.chart.width = 550

    d.title._text = request.session.get('Some custom title')

    d.XLabel._text = request.session.get('X Axis Labdasell')
    d.YLabel._text = request.session.get('Y Axis Labsadael')
    d.chart.data = [big_data]  #,((1,2), (2,3), (2.5,2), (3.5,5), (4,6))]

    labels = ["Label One", ]
    if labels:
        # set colors in the legend
        d.Legend.colorNamePairs = []
        for cnt, label in enumerate(labels):
            d.Legend.colorNamePairs.append((d.chart.lines[cnt].strokeColor, label))


    #get a GIF (or PNG, JPG, or whatever)
    binaryStuff = d.asString('png')
    return HttpResponse(binaryStuff, 'image/png')


def cpu_temp_db():
    big_data = {}
    big_data['data'] = []
    big_data['temp'] = []

    data_i = 1
    for str_temp in CPUTemperature.objects.all():
        big_data['temp'].append(float(str(str_temp).split(' ')[1]))
        big_data['data'].append([data_i, str(datetime.fromtimestamp(int(str(str_temp).split(' ')[0])).strftime('%H:%M:%S %d/%m/%Y'))])
        data_i += 1
    return big_data


@property
def graph_png():
    MyLineChartDrawing().save(formats=['png'], outDir='../static/img', fnRoot='dynamic_graph')


if __name__ == '__main__':
    # use the standard 'save' method to save barchart.gif, barchart.pdf etc
    # for quick feedback while working.
    #MyLineChartDrawing().save(formats=['png'],outDir='.',fnRoot='dynamic_graph')
    items = CPUTemperature.objects.all()