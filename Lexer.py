import re
import sys
import string

keyword = ['MAKE', 'GENERATE', 'PALETTE', 'COLOR', 'COLORS', 'FROM', 'TO', ':']
colors = ['RED', 'GREEN', 'BLUE', 'YELLOW', 'BLACK', 'BROWN', 'ORANGE']
adj = ['LIGHT', 'DARK']

class Lexel:
    def __init__(this, str):
        this.str = str
        this.tokenList, this.symbols, this.token = this.lexical()
        this.errorMessege = this.error(this.symbols)
        
    def lexical(this):
        
        this.tokens = re.findall(r'\w+|[^\w\s]', this.str)
        tokens_type = []
        symbols = []


        for i in this.tokens:
            word = i.upper()
            if word in keyword:
                tokens_type.append(('keyword', i))
            elif word in colors:
                tokens_type.append(('color', i))
            elif word in adj:
                tokens_type.append(('adj', i))
            elif re.fullmatch(r'\d+', i):
                tokens_type.append(('number', i))
            else:
                if i != ' ':
                    i = i.strip()
                    symbols.append(i)
                    tokens_type.append(('symbol', i))
            
        return tokens_type, symbols, this.tokens
    

    def error(this, symbols):
        for i in symbols:
            if(i not in r'():'):
                print(f'\"{i}\" is not define in coolor langauge')
                sys.exit(1)


class Parser:
    def __init__(this, str: string):
        this.tokens_type, this.symbols, this.token = Lexel(str).lexical()
        this.counter = 0
        this.size = len(this.token)
        
    
    def move(this):
        if this.counter < this.size:
            this.counter = this.counter + 1
        

    def expect(this, word):
        if this.token[this.counter].upper() == word:
            this.move()
        else:
            print(f'Syntax error: Expect {word} found {this.token[this.counter]}')
            sys.exit(1)

    def expect_any(this, words):
        if this.token[this.counter].upper() in words:
            this.move()
        else:
            print(f'Syntax error: Expect one of {words} found {this.token[this.counter]}')
            sys.exit(1)



    def parse(this):

        this.expect_any(["GENERATE", "MAKE"])
        this.expect("PALETTE")

        if this.token[this.counter] < '2' or this.token[this.counter] > '9':
            print(f'Syntax error: Expect number from 2 to 9 found {this.token[this.counter]}')
            sys.exit(1)
        else:
            numberOfColors = int(this.token[this.counter])

        this.move()

        this.expect_any(['COLOR', 'COLORS'])
        this.expect('FROM')

        if this.token[this.counter] == '(':
            this.move()

        
        if this.token[this.counter].upper() == 'DARK' or 'LIGHT':
            colorOneAdj = this.token[this.counter]
            this.move()
        else:
            print(f'Syntax error: Expect adj found {this.token[this.counter]}')
            sys.exit(1)

        
        if this.token[this.counter].upper() in colors:
            colorOne = this.token[this.counter]
            this.move()
        else:
            print(f'Syntax error: Expect color found {this.token[this.counter]}')
            sys.exit(1)

        this.expect_any([':', 'TO'])

        if this.token[this.counter].upper() == 'DARK' or 'LIGHT':
            colorTwoAdj = this.token[this.counter]
            this.move()
        else:
            print(f'Syntax error: Expect adj found {this.token[this.counter]}')
            sys.exit(1)

        if this.token[this.counter].upper() in colors:
            colorTwo = this.token[this.counter]
            this.move()
        else:
            print(f'Syntax error: Expect color found {this.token[this.counter]}')
            sys.exit(1)

    
        if this.counter < len(this.token) and this.token[this.counter] == ')':
            this.move()

        return {
            "cmd": f"{this.token[0]} {this.token[1]}",
            "number": numberOfColors,
            "first color": f"{colorOneAdj} {colorOne}",
            "second color": f"{colorTwoAdj} {colorTwo}"
        }




# str = r'GENERATE paLette 5 COLORS FROM (light blue TO dark orange) '
str2 = r'Make paLette 6 COLORS FROM (light blue TO dark orange) '

str3 = r'generate PALETTE 6 colors FROM light blue : dark orange '

result = Parser(str3).parse()

for k, v in result.items():
    print(f"{k}: {v}")
