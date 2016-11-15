import re
import argparse
from enum import Enum

class MatchCase(Enum):
    """ Indicates match cases for LCS algorithm. """
    NONE = 0
    TARGET = 1
    RETRIEVED = 2
    BOTH = 3    # current characters match
    SPACE = 4


def alignFiles(targetPath, retrievedPath, outputPath):
    """
    Scans two input files line by line and aligns the spaces of the retrived file with the target file.
    Assumes files contain the same number of lines.
    Writes the result in the file at outputPath.
    """
    target = open(targetPath, 'rb')
    retrieved = open(retrievedPath, 'rb')
    output = open(outputPath, 'wb')
    for targetLine in target:
        retrievedLine = retrieved.readline()
        targetLine = targetLine.decode('utf-8')
        retrievedLine = retrievedLine.decode('utf-8')
        aligned = LCSAlign(targetLine, retrievedLine)
        output.write(aligned.encode('utf-8'))
    output.flush()
    output.close()
    retrieved.close()
    target.close()
    

def LCSAlign(target, retrieved):
    """Drops all spaces from retrieved string and tries to insert spaces so as to maximize LCS match between the two strings."""
    retrieved = re.sub(r' ', '', retrieved)
    match = [[MatchCase.NONE for i in range(len(retrieved)+1)] for j in range(len(target)+1)]
    longest = [[0 for i in range(len(retrieved)+1)] for j in range(len(target)+1)]
    for i in range(len(target)):
        for j in range(len(retrieved)):
            if target[i] == retrieved[j]:
                longest[i+1][j+1] = longest[i][j] + 1
                match[i+1][j+1] = MatchCase.BOTH
            elif target[i] == ' ':
                longest[i+1][j+1] = longest[i][j+1] + 1
                match[i+1][j+1] = MatchCase.SPACE
            elif longest[i+1][j] >= longest[i][j+1]:
                longest[i+1][j+1] = longest[i+1][j]
                match[i+1][j+1] = MatchCase.TARGET
            else:
                longest[i+1][j+1] = longest[i][j+1]
                match[i+1][j+1] = MatchCase.RETRIEVED
    result = u''
    targetInd = len(target)
    retrievedInd = len(retrieved)
    while retrievedInd > 0:
        matchType = match[targetInd][retrievedInd]
        if matchType == MatchCase.BOTH:
            result = target[targetInd-1] + result
            targetInd -= 1
            retrievedInd -= 1
        elif matchType == MatchCase.SPACE:
            result = u' ' + result
            targetInd -= 1
        elif matchType == MatchCase.TARGET:
            result = retrieved[retrievedInd-1] + result
            retrievedInd -= 1
        elif matchType == MatchCase.RETRIEVED:
            targetInd -= 1
    return result


def main():
    parser = argparse.ArgumentParser(description='Aligns spaces of a retrieved file with those of a target file.')
    parser.add_argument('targetPath', type=str, help='path of the file containing target text')
    parser.add_argument('retrievedPath', type=str, help='path of the file containing retrieved text')
    parser.add_argument('outputPath', type=str, help='path of the output file')
    args = parser.parse_args()
    alignFiles(args.targetPath, args.retrievedPath, args.outputPath)


def testMain():
    for i in range(1, 6):
        target = 'tests/b{}_target.txt'.format(i)
        retrieved = 'tests/b{}_ocred.txt'.format(i)
        output = 'tests/b{}_result.txt'.format(i)
        alignFiles(target, retrieved, output)
    

if __name__=='__main__':
    main()
