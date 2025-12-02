TT_INT = 'TT_INT'
TT_FLOAT = 'TT_FLOAT'
TT_PLUS = 'TT_PLUS'
TT_MIN = 'TT_MIN'
TT_DIV = 'TT_DIV'
TT_MUL = 'TT_MUL'
TT_LPAR = 'TT_LPAR'
TT_RPAR = 'TT_RPAR'
DIGITS = '0123456789'
class Error:
    def __init__(self,err_name, details):
        self.err_name = err_name
        self.details = details
    def as_string(self):
       result = f'{self.err_name}: {self.details}'
       return result

class IllegalCharErr(Error):
    def __init__(self,details):
        super().__init__('Illegal Character',details)

class Token:
    def __init__(self,type,value=None):
        self.type = type
        self.value = value
    def __repr__(self):
        if self.value: return f'{self.type}:{self.value}'
        return f'{self.type}'
class Lexer:
    def __init__(self,text):
        self.text = text
        self.pos = -1
        self.curr_char = None
        self.advance()
    def advance(self):
        self.pos +=1
        self.curr_char = self.text[self.pos] if self.pos < len(self.text) else None
    def tokenize(self):
        tokens = []

        while self.curr_char != None:
            if self.curr_char in ' \t':
                self.advance()
            elif self.curr_char in DIGITS:
                tokens.append(self.make_numbers())
                self.advance()
            elif self.curr_char == '+':
                tokens.append(Token(TT_PLUS))
                self.advance()
            elif self.curr_char == '-':
                tokens.append(Token(TT_MIN))
                self.advance()
            elif self.curr_char == '/':
                tokens.append(Token(TT_DIV))
                self.advance()
            elif self.curr_char == '*':
                tokens.append(Token(TT_MUL))
                self.advance()
            elif self.curr_char == '(':
                tokens.append(Token(TT_LPAR))
                self.advance()
            elif self.curr_char == ')':
                tokens.append(Token(TT_RPAR))
                self.advance()
            else:
                ch = self.curr_char
                self.advance()
                return [],IllegalCharErr("'"+ch+"'")
        return tokens, None
    def make_numbers(self):
        num_str = ''
        dot_num = 0
        while self.curr_char != None and self.curr_char in DIGITS+'.':
            if self.curr_char =='.':
                if dot_num == 1: break
                dot_num +=1
                num_str +='.'
            else:
                num_str += self.curr_char
            self.advance()
        if dot_num == 1:
            return Token(TT_FLOAT,float(num_str))
        else:
            return Token(TT_INT,int(num_str))


def run(text):
    lexer = Lexer(text)
    tokens,errors = lexer.tokenize()
    return tokens,errors
