[condition][]Viene un mensaje=item : Message()

[condition][]Tengo un mensaje con id {numero}=item : Message(numMessage{numero})

[consequence][]Imprimir id=item.addOutput("columna3" , item.getInput("input1"));

[consequence][]Escribir {texto}=item.addOutput("columna4" , {texto});
